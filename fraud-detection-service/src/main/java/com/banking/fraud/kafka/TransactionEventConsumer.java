package com.banking.fraud.kafka;

import com.banking.common.constants.KafkaTopics;
import com.banking.common.event.TransactionCompletedEvent;
import com.banking.fraud.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final FraudAnalysisService fraudAnalysisService;

    @KafkaListener(
            topics = KafkaTopics.TRANSACTION_EVENTS,
            groupId = "fraud-detection-service",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void onTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Analyzing transaction ref={} amount={}", event.referenceNumber(), event.amount());
        fraudAnalysisService.analyzeTransaction(event);
    }
}
