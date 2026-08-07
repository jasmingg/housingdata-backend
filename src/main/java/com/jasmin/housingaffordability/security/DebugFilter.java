package com.jasmin.housingaffordability.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(2) // runs AFTER the rate limit filter (which we'll set to @Order(1))
public class DebugFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(DebugFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only filter requests to /api/debug/*
        String path = request.getRequestURI();
        return !path.startsWith("/api/debug");
    }

    // checks X-Debug-Token header against the DEBUG_API_TOKEN
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = req.getHeader("X-Debug-Token");

        // Read the token from environment variable; if not set, fail closed
        // 
        String expected = System.getenv("DEBUG_API_TOKEN");
        if (expected == null || expected.isEmpty()) {
            log.error("DEBUG_API_TOKEN environment variable is not set – debug endpoints are locked");
            res.sendError(503, "Error: Not authorized to access");
            return;
        }

        if (token == null || !expected.equals(token)) {
            log.warn("Forbidden request to {} from IP {}",
                     req.getRequestURI(),
                     req.getHeader("CF-Connecting-IP") != null
                         ? req.getHeader("CF-Connecting-IP")
                         : req.getRemoteAddr());
            res.sendError(403, "Forbidden: valid X-Debug-Token header required");
            return;
        }

        // Token matches – proceed
        chain.doFilter(req, res);
    }
}