package com.banking.fraud.repository;

import com.banking.fraud.entity.FraudTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface FraudTransactionLogRepository extends JpaRepository<FraudTransactionLog, UUID> {

    long countByUserIdAndProcessedAtAfter(UUID userId, Instant since);
}
