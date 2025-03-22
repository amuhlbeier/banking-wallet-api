package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.BankAccountEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BankAccountEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountEventListener.class);

    @KafkaListener(topics = "account-events", groupId = "bank-group", containerFactory = "bankAccountKafkaListenerFactory")
    public void handleBankAccountEvent(BankAccountEvent event) {
        logger.info("Received Kafka Event: {}", event);
    }
}
