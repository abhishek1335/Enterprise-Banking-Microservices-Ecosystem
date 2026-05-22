package com.banking.transaction.service;

import com.banking.transaction.dto.request.TransferRequest;
import com.banking.transaction.event.TransactionEventPublisher;
import com.banking.transaction.repository.TransactionRepository;
import com.banking.transaction.security.UserPrincipal;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountServiceFacade accountServiceFacade;
    @Mock
    private TransactionEventPublisher eventPublisher;

    @InjectMocks
    private TransactionService transactionService;

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
    void transfer_sameAccount_throws() {
        UUID accountId = UUID.randomUUID();
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(accountId);
        request.setToAccountId(accountId);
        request.setAmount(new BigDecimal("10"));

        assertThrows(Exception.class, () -> transactionService.transfer(request));
    }

    @Test
    void transfer_depositFails_compensates() {
        UUID from = UUID.randomUUID();
        UUID to = UUID.randomUUID();
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(from);
        request.setToAccountId(to);
        request.setAmount(new BigDecimal("50"));

        when(accountServiceFacade.getCurrency(from)).thenReturn("USD");
        doThrow(new RuntimeException("deposit failed"))
                .when(accountServiceFacade).deposit(to, request.getAmount());

        assertThrows(Exception.class, () -> transactionService.transfer(request));

        verify(accountServiceFacade).withdraw(from, request.getAmount());
        verify(accountServiceFacade).deposit(from, request.getAmount());
    }
}
