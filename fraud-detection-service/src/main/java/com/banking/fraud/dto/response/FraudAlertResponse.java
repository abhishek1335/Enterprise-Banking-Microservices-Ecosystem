package com.banking.fraud.dto.response;

import com.banking.fraud.entity.FraudAlertStatus;
import com.banking.fraud.entity.FraudRuleCode;
import com.banking.fraud.entity.FraudSeverity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class FraudAlertResponse {

    private UUID id;
    private UUID transactionId;
    private String referenceNumber;
    private FraudRuleCode ruleCode;
    private FraudSeverity severity;
    private String description;
    private BigDecimal amount;
    private String currency;
    private FraudAlertStatus status;
    private Instant createdAt;
}
