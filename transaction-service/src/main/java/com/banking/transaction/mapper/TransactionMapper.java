package com.banking.transaction.mapper;

import com.banking.transaction.dto.response.TransactionResponse;
import com.banking.transaction.entity.TransactionRecord;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponse toResponse(TransactionRecord record) {
        return TransactionResponse.builder()
                .id(record.getId())
                .referenceNumber(record.getReferenceNumber())
                .fromAccountId(record.getFromAccountId())
                .toAccountId(record.getToAccountId())
                .amount(record.getAmount())
                .currency(record.getCurrency())
                .transactionType(record.getTransactionType())
                .status(record.getStatus())
                .description(record.getDescription())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
