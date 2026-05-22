package com.banking.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "banking.jwt")
public class JwtProperties {

    /**
     * HS256 secret — must match auth-service (Step 5). Override via JWT_SECRET env in production.
     */
    private String secret = "change-me-use-32-char-minimum-secret-key-for-hs256-banking";

    private String issuer = "banking-system";

    private List<String> publicPaths = new ArrayList<>(List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/actuator/health",
            "/actuator/info"
    ));

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }
}
