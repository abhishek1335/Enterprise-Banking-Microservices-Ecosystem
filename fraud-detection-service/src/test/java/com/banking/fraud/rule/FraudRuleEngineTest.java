package com.banking.fraud.rule;

import com.banking.common.enums.TransactionType;
import com.banking.common.event.TransactionCompletedEvent;
import com.banking.fraud.config.FraudProperties;
import com.banking.fraud.entity.FraudRuleCode;
import com.banking.fraud.repository.FraudTransactionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudRuleEngineTest {

    @Mock
    private FraudTransactionLogRepository transactionLogRepository;

    private FraudRuleEngine engine;
    private FraudProperties properties;

    @BeforeEach
    void setUp() {
        properties = new FraudProperties();
        properties.setHighAmountThreshold(new BigDecimal("5000"));
        properties.setVelocityMaxTransfers(3);
        properties.setVelocityWindowMinutes(5);
        engine = new FraudRuleEngine(properties, transactionLogRepository);
    }

    @Test
    void evaluate_highAmount_flagsAlert() {
        TransactionCompletedEvent event = event(new BigDecimal("6000.00"));
        var results = engine.evaluate(event, 1);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRuleCode()).isEqualTo(FraudRuleCode.HIGH_AMOUNT);
    }

    @Test
    void evaluate_velocity_flagsWhenAtLimit() {
        TransactionCompletedEvent event = event(new BigDecimal("50.00"));
        var results = engine.evaluate(event, 3);
        assertThat(results).extracting(FraudRuleResult::getRuleCode).contains(FraudRuleCode.VELOCITY);
    }

    @Test
    void evaluate_smallAmount_noFlags() {
        TransactionCompletedEvent event = event(new BigDecimal("100.00"));
        assertThat(engine.evaluate(event, 1)).isEmpty();
    }

    @Test
    void countRecentTransfers_delegatesToRepository() {
        UUID userId = UUID.randomUUID();
        when(transactionLogRepository.countByUserIdAndProcessedAtAfter(eq(userId), any())).thenReturn(2L);
        assertThat(engine.countRecentTransfers(userId)).isEqualTo(2L);
    }

    private TransactionCompletedEvent event(BigDecimal amount) {
        return new TransactionCompletedEvent(
                UUID.randomUUID(),
                "TXN-TEST",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                amount,
                "USD",
                TransactionType.TRANSFER,
                "COMPLETED",
                Instant.now()
        );
    }
}
