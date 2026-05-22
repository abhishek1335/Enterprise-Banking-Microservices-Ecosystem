package com.banking.transaction.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BalanceFeignResponse {

    private boolean success;
    private String message;
    private BalanceData data;

    @Data
    public static class BalanceData {
        private UUID accountId;
        private String accountNumber;
        private BigDecimal balance;
        private String currency;
    }
}
