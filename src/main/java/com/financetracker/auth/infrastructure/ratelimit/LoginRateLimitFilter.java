package com.financetracker.auth.infrastructure.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final Duration refillPeriod;

    public LoginRateLimitFilter() {
        this.capacity = 5;
        this.refillPeriod = Duration.ofMinutes(1);
    }

    public LoginRateLimitFilter(int capacity, int refillMinutes) {
        this.capacity = capacity;
        this.refillPeriod = Duration.ofMinutes(refillMinutes);
    }

    private Bucket bucketFor(String ip) {
        return buckets.computeIfAbsent(ip, key -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillGreedy(capacity, refillPeriod)
                        .build())
                .build());
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain chain)
            throws ServletException, IOException {
        Bucket bucket = bucketFor(request.getRemoteAddr());
        long available = bucket.getAvailableTokens();

        response.setHeader("X-RateLimit-Limit", String.valueOf(capacity));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, available - 1)));
        response.setHeader("X-RateLimit-Reset", String.valueOf(
                System.currentTimeMillis() / 1000 + refillPeriod.getSeconds()));

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(refillPeriod.getSeconds()));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"status":429,"message":"Too many requests. Please try again later.","errors":[]}""");
        }
    }
}
