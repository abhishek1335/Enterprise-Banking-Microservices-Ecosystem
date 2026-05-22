package com.banking.gateway.filter;

import com.banking.gateway.config.RateLimitProperties;
import com.banking.gateway.util.GatewayResponseWriter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token-bucket rate limiter per client IP (dev/single-node).
 * Production clusters should use Redis-backed RequestRateLimiter.
 */
@Component
public class RateLimitGatewayFilter implements GlobalFilter, Ordered {

    private final RateLimitProperties properties;
    private final GatewayResponseWriter responseWriter;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitGatewayFilter(RateLimitProperties properties, GatewayResponseWriter responseWriter) {
        this.properties = properties;
        this.responseWriter = responseWriter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }

        String clientKey = resolveClientKey(exchange);
        Bucket bucket = buckets.computeIfAbsent(clientKey, key -> createBucket());

        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().getHeaders().add("X-Rate-Limit-Retry-After-Seconds", "1");
        return responseWriter.writeError(exchange, HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(
                properties.getBurstCapacity(),
                Refill.greedy(properties.getReplenishPerSecond(), Duration.ofSeconds(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveClientKey(ServerWebExchange exchange) {
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        if (exchange.getRequest().getRemoteAddress() != null
                && exchange.getRequest().getRemoteAddress().getAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
