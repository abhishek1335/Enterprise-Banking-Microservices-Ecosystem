package com.banking.transaction.event;

import com.banking.common.event.TransactionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "banking.kafka.enabled", havingValue = "false")
public class LoggingTransactionEventPublisher implements TransactionEventPublisher {

    @Override
    public void publish(TransactionCompletedEvent event) {
        log.info("Kafka disabled — transaction event: {}", event);
    }
}
