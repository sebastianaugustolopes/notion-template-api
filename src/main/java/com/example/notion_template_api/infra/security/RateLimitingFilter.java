package com.example.notion_template_api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple rate limiting filter to prevent brute force attacks on login endpoint.
 * Allows 5 failed login attempts per IP address per 15 minutes.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long TIME_WINDOW_MS = 15 * 60 * 1000; // 15 minutes
    private static final Map<String, AttemptTracker> attemptMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        // Only apply rate limiting to login endpoint
        if (path.equals("/auth/login") && "POST".equalsIgnoreCase(request.getMethod())) {
            String clientIp = getClientIp(request);
            
            if (!isRequestAllowed(clientIp)) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many login attempts. Please try again later.\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isRequestAllowed(String clientIp) {
        long now = System.currentTimeMillis();
        AttemptTracker tracker = attemptMap.get(clientIp);
        
        if (tracker == null) {
            attemptMap.put(clientIp, new AttemptTracker(now));
            return true;
        }
        
        // Reset if time window has passed
        if (now - tracker.firstAttemptTime > TIME_WINDOW_MS) {
            tracker.reset(now);
            return true;
        }
        
        // Check if max attempts exceeded
        if (tracker.attemptCount >= MAX_ATTEMPTS) {
            return false;
        }
        
        tracker.attemptCount++;
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class AttemptTracker {
        long firstAttemptTime;
        int attemptCount;

        AttemptTracker(long firstAttemptTime) {
            this.firstAttemptTime = firstAttemptTime;
            this.attemptCount = 1;
        }

        void reset(long newTime) {
            this.firstAttemptTime = newTime;
            this.attemptCount = 1;
        }
    }
}
