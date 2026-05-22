package com.banking.transaction.service;

import com.banking.transaction.client.AccountClient;
import com.banking.transaction.client.dto.AccountFeignResponse;
import com.banking.transaction.client.dto.AmountFeignRequest;
import com.banking.transaction.client.dto.BalanceFeignResponse;
import com.banking.common.exception.BusinessException;
import com.banking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountServiceFacade {

    private final AccountClient accountClient;

    public void ensureAccountExists(UUID accountId) {
        ensureSuccess(accountClient.getAccount(accountId));
    }

    public void validateOwnership(UUID accountId, UUID userId) {
        AccountFeignResponse response = accountClient.getAccount(accountId);
        ensureSuccess(response);
        if (response.getData() == null || !userId.equals(response.getData().getUserId())) {
            throw new ResourceNotFoundException("Account not found");
        }
    }

    public void validateBalance(UUID accountId, BigDecimal amount) {
        BalanceFeignResponse response = accountClient.getBalance(accountId);
        ensureSuccess(response);
        if (response.getData() == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        if (response.getData().getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient balance", HttpStatus.BAD_REQUEST);
        }
    }

    public void withdraw(UUID accountId, BigDecimal amount) {
        AccountFeignResponse response = accountClient.withdraw(accountId, new AmountFeignRequest(amount));
        ensureSuccess(response);
    }

    public void deposit(UUID accountId, BigDecimal amount) {
        AccountFeignResponse response = accountClient.deposit(accountId, new AmountFeignRequest(amount));
        ensureSuccess(response);
    }

    public String getCurrency(UUID accountId) {
        BalanceFeignResponse response = accountClient.getBalance(accountId);
        ensureSuccess(response);
        return response.getData() != null ? response.getData().getCurrency() : "USD";
    }

    private void ensureSuccess(AccountFeignResponse response) {
        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw new BusinessException("Account service call failed", HttpStatus.BAD_GATEWAY);
        }
    }

    private void ensureSuccess(BalanceFeignResponse response) {
        if (response == null || !response.isSuccess() || response.getData() == null) {
            throw new BusinessException("Account service call failed", HttpStatus.BAD_GATEWAY);
        }
    }
}
