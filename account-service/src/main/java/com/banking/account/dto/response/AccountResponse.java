package com.banking.account.dto.response;

import com.banking.account.entity.AccountStatus;
import com.banking.common.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AccountResponse {

    private UUID id;
    private UUID userId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private Instant createdAt;
}
