package com.banking.fraud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "banking.fraud")
public class FraudProperties {

    private BigDecimal highAmountThreshold = new BigDecimal("5000");
    private int velocityWindowMinutes = 5;
    private int velocityMaxTransfers = 3;
}
