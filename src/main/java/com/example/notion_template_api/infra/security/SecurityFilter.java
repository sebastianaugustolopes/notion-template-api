package com.example.notion_template_api.infra.security;

import com.example.notion_template_api.domain.user.User;
import com.example.notion_template_api.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Allow preflight CORS (OPTIONS) requests to pass through directly
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();

        // Allow public routes without authentication
        if (path.equals("/auth/login") || path.equals("/auth/register") || path.equals("/auth/signup") ||
            path.equals("/api/auth/login") || path.equals("/api/auth/register") || path.equals("/api/auth/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = this.recoverToken(request);
        var login = tokenService.validateToken(token);

        if (login != null) {
            User user = userRepository.findByEmail(login)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));

            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // Invalid, expired, or missing token on protected route
            // Return 401 Unauthorized instead of continuing without authentication
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized: Invalid or missing token\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
