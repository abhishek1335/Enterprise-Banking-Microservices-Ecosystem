package com.banking.fraud.mapper;

import com.banking.fraud.dto.response.FraudAlertResponse;
import com.banking.fraud.entity.FraudAlert;

public final class FraudAlertMapper {

    private FraudAlertMapper() {
    }

    public static FraudAlertResponse toResponse(FraudAlert alert) {
        return FraudAlertResponse.builder()
                .id(alert.getId())
                .transactionId(alert.getTransactionId())
                .referenceNumber(alert.getReferenceNumber())
                .ruleCode(alert.getRuleCode())
                .severity(alert.getSeverity())
                .description(alert.getDescription())
                .amount(alert.getAmount())
                .currency(alert.getCurrency())
                .status(alert.getStatus())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
