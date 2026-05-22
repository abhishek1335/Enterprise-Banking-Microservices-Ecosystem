package com.banking.account.service;

import com.banking.account.dto.request.AmountRequest;
import com.banking.account.dto.request.CreateAccountRequest;
import com.banking.account.dto.response.AccountResponse;
import com.banking.account.dto.response.BalanceResponse;
import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.mapper.AccountMapper;
import com.banking.account.repository.AccountRepository;
import com.banking.account.security.UserPrincipal;
import com.banking.common.exception.BusinessException;
import com.banking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        UUID userId = currentUserId();
        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .status(AccountStatus.ACTIVE)
                .build();

        Account saved = accountRepository.save(account);
        log.info("Created account {} for user {}", saved.getAccountNumber(), userId);
        return AccountMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID accountId) {
        Account account = findOwnedAccount(accountId);
        return AccountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listMyAccounts() {
        return accountRepository.findByUserId(currentUserId()).stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(UUID accountId) {
        return AccountMapper.toBalance(findOwnedAccount(accountId));
    }

    @Transactional
    public AccountResponse deposit(UUID accountId, AmountRequest request) {
        Account account = findOwnedAccount(accountId);
        ensureActive(account);
        account.setBalance(account.getBalance().add(request.getAmount()));
        Account updated = accountRepository.save(account);
        log.info("Deposit {} to account {}", request.getAmount(), account.getAccountNumber());
        return AccountMapper.toResponse(updated);
    }

    @Transactional
    public AccountResponse withdraw(UUID accountId, AmountRequest request) {
        Account account = findOwnedAccount(accountId);
        ensureActive(account);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance", HttpStatus.BAD_REQUEST);
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        Account updated = accountRepository.save(account);
        log.info("Withdraw {} from account {}", request.getAmount(), account.getAccountNumber());
        return AccountMapper.toResponse(updated);
    }

    private Account findOwnedAccount(UUID accountId) {
        return accountRepository.findByIdAndUserId(accountId, currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private void ensureActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active", HttpStatus.BAD_REQUEST);
        }
    }

    private UUID currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }
        throw new BusinessException("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    private String generateAccountNumber() {
        String candidate;
        do {
            candidate = "ACC" + System.currentTimeMillis() % 1_000_000_000_00L
                    + ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (accountRepository.existsByAccountNumber(candidate));
        return candidate;
    }
}
