package com.banking.fraud.repository;

import com.banking.fraud.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, UUID> {

    List<FraudAlert> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<FraudAlert> findByIdAndUserId(UUID id, UUID userId);
}
