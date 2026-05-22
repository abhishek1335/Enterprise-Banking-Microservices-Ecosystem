package com.banking.gateway.util;

import com.banking.gateway.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private JwtProperties properties;

    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        properties.setSecret("test-secret-key-minimum-32-characters-long-hs256");
        properties.setIssuer("banking-system");
        jwtUtil = new JwtUtil(properties);
    }

    @Test
    void parseAndValidate_acceptsValidToken() {
        String token = buildToken("user-123", List.of("ROLE_CUSTOMER"));
        var claims = jwtUtil.parseAndValidate(token);

        assertEquals("user-123", jwtUtil.extractUserId(claims));
        assertEquals("ROLE_CUSTOMER", jwtUtil.extractRolesHeader(claims));
    }

    @Test
    void parseAndValidate_rejectsWrongIssuer() {
        SecretKey key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("user-1")
                .issuer("wrong-issuer")
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(key)
                .compact();

        assertThrows(Exception.class, () -> jwtUtil.parseAndValidate(token));
    }

    private String buildToken(String subject, List<String> roles) {
        SecretKey key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(subject)
                .issuer(properties.getIssuer())
                .claim("roles", roles)
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(key)
                .compact();
    }
}
