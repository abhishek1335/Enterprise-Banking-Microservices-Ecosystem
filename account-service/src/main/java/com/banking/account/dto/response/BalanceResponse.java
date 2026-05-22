package com.banking.account.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class BalanceResponse {

    private UUID accountId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
}
