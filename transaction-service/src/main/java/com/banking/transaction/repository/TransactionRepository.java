package com.banking.transaction.repository;

import com.banking.transaction.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionRecord, UUID> {

    @Query("""
            SELECT t FROM TransactionRecord t
            WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId
            ORDER BY t.createdAt DESC
            """)
    List<TransactionRecord> findHistoryByAccountId(@Param("accountId") UUID accountId);

    List<TransactionRecord> findByInitiatedByOrderByCreatedAtDesc(UUID userId);
}
