package com.banking.transaction.event;

import com.banking.common.constants.KafkaTopics;
import com.banking.common.event.TransactionCompletedEvent;
import com.banking.transaction.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "banking.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTransactionEventPublisher implements TransactionEventPublisher {

    private final KafkaTemplate<String, TransactionCompletedEvent> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    @Override
    public void publish(TransactionCompletedEvent event) {
        if (!kafkaProperties.isEnabled()) {
            log.debug("Kafka disabled — skipping event {}", event.referenceNumber());
            return;
        }
        kafkaTemplate.send(KafkaTopics.TRANSACTION_EVENTS, event.referenceNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish transaction event {}", event.referenceNumber(), ex);
                    } else {
                        log.info("Published transaction event {} to Kafka", event.referenceNumber());
                    }
                });
    }
}
