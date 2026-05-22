package com.banking.transaction.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "banking.kafka")
public class KafkaProperties {

    private boolean enabled = true;
}
