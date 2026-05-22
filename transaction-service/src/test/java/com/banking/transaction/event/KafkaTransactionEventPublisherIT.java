package com.banking.transaction.event;

import com.banking.common.constants.KafkaTopics;
import com.banking.common.enums.TransactionType;
import com.banking.common.event.TransactionCompletedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka(partitions = 1, topics = KafkaTopics.TRANSACTION_EVENTS)
class KafkaTransactionEventPublisherIT {

    @Test
    void publishedMessage_isReadableFromTopic(EmbeddedKafkaBroker broker) {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(broker.getBrokersAsString());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaTemplate<String, TransactionCompletedEvent> template =
                new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

        TransactionCompletedEvent event = new TransactionCompletedEvent(
                UUID.randomUUID(),
                "TXN-TEST-001",
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("99.50"),
                "USD",
                TransactionType.TRANSFER,
                "COMPLETED",
                Instant.now()
        );

        template.send(KafkaTopics.TRANSACTION_EVENTS, event.referenceNumber(), event).join();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", broker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.banking.common.event");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TransactionCompletedEvent.class.getName());

        try (Consumer<String, TransactionCompletedEvent> consumer =
                     new DefaultKafkaConsumerFactory<>(
                             consumerProps,
                             new StringDeserializer(),
                             new JsonDeserializer<>(TransactionCompletedEvent.class, false)
                     ).createConsumer()) {
            consumer.subscribe(java.util.List.of(KafkaTopics.TRANSACTION_EVENTS));
            ConsumerRecords<String, TransactionCompletedEvent> records =
                    KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));

            assertThat(records.count()).isGreaterThanOrEqualTo(1);
            assertThat(records.iterator().next().value().referenceNumber()).isEqualTo("TXN-TEST-001");
        }
    }
}
