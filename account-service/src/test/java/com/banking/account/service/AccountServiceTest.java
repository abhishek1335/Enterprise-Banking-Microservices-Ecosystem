package com.banking.account.service;

import com.banking.account.dto.request.AmountRequest;
import com.banking.account.dto.request.CreateAccountRequest;
import com.banking.account.entity.Account;
import com.banking.account.entity.AccountStatus;
import com.banking.account.repository.AccountRepository;
import com.banking.account.security.UserPrincipal;
import com.banking.common.enums.AccountType;
import com.banking.common.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void auth() {
        UserPrincipal principal = new UserPrincipal(userId, List.of("ROLE_CUSTOMER"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void withdraw_insufficientBalance_throws() {
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .userId(userId)
                .accountNumber("ACC123")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("50.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(account));

        AmountRequest request = new AmountRequest();
        request.setAmount(new BigDecimal("100.00"));

        assertThrows(BusinessException.class, () -> accountService.withdraw(accountId, request));
    }

    @Test
    void deposit_increasesBalance() {
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .userId(userId)
                .accountNumber("ACC123")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("100.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build();

        when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        AmountRequest request = new AmountRequest();
        request.setAmount(new BigDecimal("25.50"));

        var response = accountService.deposit(accountId, request);
        assertEquals(new BigDecimal("125.50"), response.getBalance());
    }
}
