package com.banking.gateway.filter;

import com.banking.gateway.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "banking.rate-limit.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
class JwtAuthenticationFilterIT {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtProperties jwtProperties;

    private String validToken;

    @BeforeEach
    void token() {
        SecretKey key = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        validToken = Jwts.builder()
                .subject("user-99")
                .issuer(jwtProperties.getIssuer())
                .claim("roles", List.of("ROLE_CUSTOMER"))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(key)
                .compact();
    }

    @Test
    void publicLoginPath_doesNotRequireJwt() {
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/v1/auth/login")
                .exchange()
                .expectStatus().is5xxServerError(); // upstream down, but not 401
    }

    @Test
    void protectedPath_withoutToken_returns401() {
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/v1/accounts/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedPath_withValidToken_passesJwtCheck() {
        // JWT accepted → gateway forwards; upstream absent → 503 not 401
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/v1/accounts/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
