package com.banking.transaction.event;

import com.banking.common.event.TransactionCompletedEvent;

public interface TransactionEventPublisher {

    void publish(TransactionCompletedEvent event);
}
