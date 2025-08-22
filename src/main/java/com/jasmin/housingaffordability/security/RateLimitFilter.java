package com.jasmin.housingaffordability.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

  private static final Bandwidth BANDWIDTH = Bandwidth.builder()
      .capacity(60)
      .refillIntervally(60, Duration.ofMinutes(1))
      .initialTokens(60)
      .build();

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith("/api");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    // expose headers so the browser can see them too (optional)
    res.addHeader("Access-Control-Expose-Headers",
        "X-RateLimit-Limit, X-RateLimit-Remaining, Retry-After");

    String key = clientKey(req);
    Bucket bucket = buckets.computeIfAbsent(key, k -> Bucket.builder().addLimit(BANDWIDTH).build());

    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

    long remaining = Math.max(probe.getRemainingTokens(), 0);
    res.addHeader("X-RateLimit-Limit", "180"); // changed to 180 to allow GitHub bot to generate visualizer
    res.addHeader("X-RateLimit-Remaining", String.valueOf(remaining));

    // 🔎 terminal log you want:
    log.info("rate-limit key={} remaining={}", key, remaining);

    if (probe.isConsumed()) {
      chain.doFilter(req, res);
    } else {
      long waitMs = probe.getNanosToWaitForRefill() / 1_000_000;
      res.addHeader("Retry-After", String.valueOf(Math.max(waitMs / 1000, 1)));
      res.sendError(429, "Too Many Requests");
    }
  }

  private String clientKey(HttpServletRequest req) {
    return "ip:" + req.getRemoteAddr();
  }
}