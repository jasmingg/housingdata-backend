package com.jasmin.housingaffordability.security;

import com.jasmin.housingaffordability.dto.BurdenDto;
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

  private final BurdenDto burdenDto;

  private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

  private static final int REQUESTS_PER_MINUTE = 60;

  // requests per minute (1 request per second on average)
  private static final Bandwidth BANDWIDTH = Bandwidth.builder()
      .capacity(REQUESTS_PER_MINUTE)
      .refillIntervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
      .initialTokens(REQUESTS_PER_MINUTE)
      .build();

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  RateLimitFilter(BurdenDto burdenDto) {
    this.burdenDto = burdenDto;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // applies rate limit to "/api" endpoints
      // adjust this if there are other public endpoints that need rate limiting
      // all api endpoints in controller
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
    // This header in every response tells client:
      //  how many tokens they have left in the current window
      //  and what the limit is.
    res.addHeader("X-RateLimit-Limit", String.valueOf(REQUESTS_PER_MINUTE));
    res.addHeader("X-RateLimit-Remaining", String.valueOf(remaining));

    // 🔎 logging the tokens left 
    log.info("rate-limit key={} remaining={}", key, remaining);

    if (probe.isConsumed()) {
      chain.doFilter(req, res);
    } else {
      long waitMs = probe.getNanosToWaitForRefill() / 1_000_000;
      res.addHeader("Retry-After", String.valueOf(Math.max(waitMs / 1000, 1)));
      res.sendError(429, "Too Many Requests");
    }
  }
  /*
   *  this is a critical function for this filter file:
   *  first tries to get CF-Connecting-IP header (used by Cloudflare) to get the real client IP address
   * if not present, falls back to the remote address of the request
   * 
  */
  private String clientKey(HttpServletRequest req) {
    String cfConnectingIp = req.getHeader("CF-Connecting-IP");
    if (cfConnectingIp != null && !cfConnectingIp.isEmpty()) {
      return "ip: " + cfConnectingIp;
    }
    // fallback to remote address if CF-Connecting-IP is not present
    // used when not behind Cloudflare:
      // ie when running locally or in a non-Cloudflare environment (direct-to-pi)
      // should returns the client IP address outside of the Cloudflare
    return "ip: " + req.getRemoteAddr();
  }
}