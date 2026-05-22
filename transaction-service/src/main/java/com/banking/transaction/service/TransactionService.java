package com.banking.transaction.service;

import com.banking.common.enums.TransactionType;
import com.banking.common.exception.BusinessException;
import com.banking.transaction.dto.request.TransferRequest;
import com.banking.transaction.dto.response.TransactionResponse;
import com.banking.transaction.entity.TransactionRecord;
import com.banking.transaction.entity.TransactionStatus;
import com.banking.common.event.TransactionCompletedEvent;
import com.banking.transaction.event.TransactionEventPublisher;
import com.banking.transaction.mapper.TransactionMapper;
import com.banking.transaction.repository.TransactionRepository;
import com.banking.transaction.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceFacade accountServiceFacade;
    private final TransactionEventPublisher eventPublisher;

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        UUID userId = currentUserId();

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BusinessException("Cannot transfer to the same account", HttpStatus.BAD_REQUEST);
        }

        accountServiceFacade.validateOwnership(request.getFromAccountId(), userId);
        accountServiceFacade.ensureAccountExists(request.getToAccountId());
        accountServiceFacade.validateBalance(request.getFromAccountId(), request.getAmount());

        String currency = accountServiceFacade.getCurrency(request.getFromAccountId());
        String reference = "TXN" + System.currentTimeMillis();

        accountServiceFacade.withdraw(request.getFromAccountId(), request.getAmount());
        try {
            accountServiceFacade.deposit(request.getToAccountId(), request.getAmount());
        } catch (Exception ex) {
            log.error("Deposit failed — compensating withdraw for {}", reference, ex);
            accountServiceFacade.deposit(request.getFromAccountId(), request.getAmount());
            throw new BusinessException("Transfer failed; funds restored to source account", HttpStatus.BAD_GATEWAY);
        }

        TransactionRecord record = TransactionRecord.builder()
                .referenceNumber(reference)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .initiatedBy(userId)
                .amount(request.getAmount())
                .currency(currency)
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription())
                .build();

        TransactionRecord saved = transactionRepository.save(record);

        eventPublisher.publish(new TransactionCompletedEvent(
                saved.getId(),
                saved.getReferenceNumber(),
                saved.getFromAccountId(),
                saved.getToAccountId(),
                saved.getInitiatedBy(),
                saved.getAmount(),
                saved.getCurrency(),
                saved.getTransactionType(),
                saved.getStatus().name(),
                Instant.now()
        ));

        log.info("Transfer completed ref={} amount={}", reference, request.getAmount());
        return TransactionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> historyByAccount(UUID accountId) {
        UUID userId = currentUserId();
        accountServiceFacade.validateOwnership(accountId, userId);
        return transactionRepository.findHistoryByAccountId(accountId).stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> myTransactions() {
        return transactionRepository.findByInitiatedByOrderByCreatedAtDesc(currentUserId()).stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(UUID transactionId) {
        TransactionRecord record = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new com.banking.common.exception.ResourceNotFoundException("Transaction not found"));
        UUID userId = currentUserId();
        if (!record.getInitiatedBy().equals(userId)) {
            throw new com.banking.common.exception.ResourceNotFoundException("Transaction not found");
        }
        return TransactionMapper.toResponse(record);
    }

    private UUID currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUserId();
        }
        throw new BusinessException("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
