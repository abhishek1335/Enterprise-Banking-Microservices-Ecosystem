package com.banking.transaction.dto.response;

import com.banking.common.enums.TransactionType;
import com.banking.transaction.entity.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {

    private UUID id;
    private String referenceNumber;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String currency;
    private TransactionType transactionType;
    private TransactionStatus status;
    private String description;
    private Instant createdAt;
}
