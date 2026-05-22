package com.banking.gateway.filter;

import com.banking.common.constants.ServiceConstants;
import com.banking.gateway.config.JwtProperties;
import com.banking.gateway.util.GatewayResponseWriter;
import com.banking.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validates JWT on all non-public routes and forwards identity headers to downstream services.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final GatewayResponseWriter responseWriter;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(
            JwtProperties jwtProperties,
            JwtUtil jwtUtil,
            GatewayResponseWriter responseWriter) {
        this.jwtProperties = jwtProperties;
        this.jwtUtil = jwtUtil;
        this.responseWriter = responseWriter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return responseWriter.writeError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authorization.substring(7);
        try {
            Claims claims = jwtUtil.parseAndValidate(token);
            String userId = jwtUtil.extractUserId(claims);
            String roles = jwtUtil.extractRolesHeader(claims);

            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header(ServiceConstants.HEADER_USER_ID, userId)
                    .header("X-User-Roles", roles)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (JwtException ex) {
            return responseWriter.writeError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    private boolean isPublicPath(String path) {
        return jwtProperties.getPublicPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
