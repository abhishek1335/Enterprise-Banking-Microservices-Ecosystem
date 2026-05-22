package com.banking.fraud.rule;

import com.banking.common.event.TransactionCompletedEvent;
import com.banking.fraud.config.FraudProperties;
import com.banking.fraud.repository.FraudTransactionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FraudRuleEngine {

    private final FraudProperties fraudProperties;
    private final FraudTransactionLogRepository transactionLogRepository;

    public List<FraudRuleResult> evaluate(TransactionCompletedEvent event, long recentTransferCount) {
        List<FraudRuleResult> results = new ArrayList<>();

        if (event.amount().compareTo(fraudProperties.getHighAmountThreshold()) > 0) {
            results.add(FraudRuleResult.builder()
                    .ruleCode(com.banking.fraud.entity.FraudRuleCode.HIGH_AMOUNT)
                    .severity(com.banking.fraud.entity.FraudSeverity.HIGH)
                    .description(String.format(
                            "Transfer amount %s %s exceeds threshold %s %s",
                            event.amount(), event.currency(),
                            fraudProperties.getHighAmountThreshold(), event.currency()))
                    .build());
        }

        int max = fraudProperties.getVelocityMaxTransfers();
        if (recentTransferCount >= max) {
            results.add(FraudRuleResult.builder()
                    .ruleCode(com.banking.fraud.entity.FraudRuleCode.VELOCITY)
                    .severity(com.banking.fraud.entity.FraudSeverity.MEDIUM)
                    .description(String.format(
                            "%d transfers within %d minutes (limit: %d)",
                            recentTransferCount,
                            fraudProperties.getVelocityWindowMinutes(),
                            max))
                    .build());
        }

        return results;
    }

    public long countRecentTransfers(java.util.UUID userId) {
        Instant since = Instant.now().minus(fraudProperties.getVelocityWindowMinutes(), ChronoUnit.MINUTES);
        return transactionLogRepository.countByUserIdAndProcessedAtAfter(userId, since);
    }
}
