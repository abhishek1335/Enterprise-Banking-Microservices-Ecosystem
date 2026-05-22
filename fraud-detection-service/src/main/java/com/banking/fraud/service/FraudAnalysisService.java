package com.banking.fraud.service;

import com.banking.common.event.TransactionCompletedEvent;
import com.banking.fraud.entity.FraudAlert;
import com.banking.fraud.entity.FraudTransactionLog;
import com.banking.fraud.event.FraudAlertPublisher;
import com.banking.fraud.repository.FraudAlertRepository;
import com.banking.fraud.repository.FraudTransactionLogRepository;
import com.banking.fraud.rule.FraudRuleEngine;
import com.banking.fraud.rule.FraudRuleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final FraudTransactionLogRepository transactionLogRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final FraudRuleEngine ruleEngine;
    private final FraudAlertPublisher alertPublisher;

    @Transactional
    public void analyzeTransaction(TransactionCompletedEvent event) {
        transactionLogRepository.save(FraudTransactionLog.builder()
                .userId(event.initiatedBy())
                .transactionId(event.transactionId())
                .referenceNumber(event.referenceNumber())
                .amount(event.amount())
                .currency(event.currency())
                .build());

        long recentCount = ruleEngine.countRecentTransfers(event.initiatedBy());
        List<FraudRuleResult> violations = ruleEngine.evaluate(event, recentCount);

        if (violations.isEmpty()) {
            log.debug("No fraud flags for ref={}", event.referenceNumber());
            return;
        }

        for (FraudRuleResult violation : violations) {
            FraudAlert alert = FraudAlert.builder()
                    .userId(event.initiatedBy())
                    .transactionId(event.transactionId())
                    .referenceNumber(event.referenceNumber())
                    .ruleCode(violation.getRuleCode())
                    .severity(violation.getSeverity())
                    .description(violation.getDescription())
                    .amount(event.amount())
                    .currency(event.currency())
                    .build();
            FraudAlert saved = fraudAlertRepository.save(alert);
            alertPublisher.publish(saved);
            log.warn("Fraud alert created: {} — {}", saved.getRuleCode(), saved.getDescription());
        }
    }
}
