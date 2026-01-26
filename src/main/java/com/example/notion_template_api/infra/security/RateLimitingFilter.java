package com.example.notion_template_api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple rate limiting filter to prevent brute force attacks on login endpoint.
 * Allows 5 failed login attempts per IP address per 15 minutes.
 * Only counts failed attempts (4xx/5xx status codes), successful logins reset the counter.
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
            
            // Check if rate limit exceeded before processing
            if (!isRequestAllowed(clientIp)) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many failed login attempts. Please try again later.\"}");
                return;
            }
            
            // Wrap response to capture status code
            StatusCapturingResponseWrapper wrappedResponse = new StatusCapturingResponseWrapper(response);
            filterChain.doFilter(request, wrappedResponse);
            
            // Only count failed attempts (4xx or 5xx status codes)
            int statusCode = wrappedResponse.getStatus();
            if (statusCode >= 400) {
                recordFailedAttempt(clientIp);
            } else if (statusCode == 200) {
                // Successful login - reset attempts for this IP
                resetAttempts(clientIp);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isRequestAllowed(String clientIp) {
        long now = System.currentTimeMillis();
        AttemptTracker tracker = attemptMap.get(clientIp);
        
        if (tracker == null) {
            return true;
        }
        
        // Reset if time window has passed
        if (now - tracker.firstAttemptTime > TIME_WINDOW_MS) {
            attemptMap.remove(clientIp);
            return true;
        }
        
        // Check if max attempts exceeded
        return tracker.attemptCount < MAX_ATTEMPTS;
    }
    
    private void recordFailedAttempt(String clientIp) {
        long now = System.currentTimeMillis();
        AttemptTracker tracker = attemptMap.get(clientIp);
        
        if (tracker == null) {
            attemptMap.put(clientIp, new AttemptTracker(now));
        } else {
            // Reset if time window has passed
            if (now - tracker.firstAttemptTime > TIME_WINDOW_MS) {
                tracker.reset(now);
            } else {
                tracker.attemptCount++;
            }
        }
    }
    
    private void resetAttempts(String clientIp) {
        attemptMap.remove(clientIp);
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
    
    /**
     * Wrapper to capture HTTP status code from response
     */
    private static class StatusCapturingResponseWrapper extends HttpServletResponseWrapper {
        private int status = 200;

        public StatusCapturingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            status = sc;
            super.setStatus(sc);
        }

        @Override
        public void sendError(int sc) throws IOException {
            status = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            status = sc;
            super.sendError(sc, msg);
        }

        public int getStatus() {
            return status;
        }
    }
}
