package com.banking.transaction.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountFeignResponse {

    private boolean success;
    private String message;
    private AccountData data;

    @Data
    public static class AccountData {
        private UUID id;
        private UUID userId;
        private String accountNumber;
        private BigDecimal balance;
        private String currency;
    }
}
