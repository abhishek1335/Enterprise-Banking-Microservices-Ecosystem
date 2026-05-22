package com.banking.fraud.event;

import com.banking.common.constants.KafkaTopics;
import com.banking.common.event.FraudAlertEvent;
import com.banking.fraud.entity.FraudAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudAlertPublisher {

    private final KafkaTemplate<String, FraudAlertEvent> kafkaTemplate;

    public void publish(FraudAlert alert) {
        FraudAlertEvent event = new FraudAlertEvent(
                alert.getId(),
                alert.getUserId(),
                alert.getTransactionId(),
                alert.getReferenceNumber(),
                alert.getRuleCode().name(),
                alert.getSeverity().name(),
                alert.getDescription(),
                alert.getAmount(),
                alert.getCurrency(),
                alert.getCreatedAt()
        );
        kafkaTemplate.send(KafkaTopics.FRAUD_ALERTS, alert.getUserId().toString(), event);
        log.info("Published fraud alert {} rule={} ref={}", alert.getId(), alert.getRuleCode(), alert.getReferenceNumber());
    }
}
