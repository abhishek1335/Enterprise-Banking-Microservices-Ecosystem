package com.banking.fraud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "banking.jwt")
public class JwtProperties {

    private String secret;
    private String issuer;
}
