package com.banking.transaction.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "banking.jwt")
public class JwtProperties {

    private String secret = "change-me-use-32-char-minimum-secret-key-for-hs256-banking";
    private String issuer = "banking-system";
}
