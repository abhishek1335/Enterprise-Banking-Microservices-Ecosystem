package com.banking.gateway.util;

import com.banking.gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Validates JWTs issued by auth-service. Gateway uses this for perimeter security.
 */
@Component
public class JwtUtil {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseAndValidate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(properties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        if (expiration != null && expiration.before(new Date())) {
            throw new JwtException("Token expired");
        }
        return claims;
    }

    public String extractUserId(Claims claims) {
        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new JwtException("Missing subject");
        }
        return subject;
    }

    @SuppressWarnings("unchecked")
    public String extractRolesHeader(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return String.join(",", list.stream().map(Object::toString).toList());
        }
        if (roles instanceof String roleString) {
            return roleString;
        }
        return "";
    }
}
