package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.BankAccountEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BankAccountEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountEventListener.class);

    @KafkaListener(
            topics = "bank-account-events",
            groupId = "bank-group",
            containerFactory = "bankAccountKafkaListenerFactory"
    )
    public void handleBankAccountEvent(BankAccountEvent event) {
        logger.info("Received Kafka Event: {}", event);

        switch (event.getEventType()) {
            case "ACCOUNT_CREATED":
                logger.info("Bank account created: {}", event.getAccountId());
                break;
            case "DEPOSIT":
                logger.info("Deposit made to account {}: ${}", event.getAccountId(), event.getBalance());
                break;
            case "OVERDRAFT_EXCEEDED":
                logger.info("Overdraft limit exceeded for account: {}", event.getAccountId());
                break;
            case "ACCOUNT_FROZEN":
                logger.info("Account {} is frozen due to overdraft", event.getAccountId());
                break;
            case "WITHDRAWAL":
                logger.info("Withdrawal made to account {}: ${}", event.getAccountId(), event.getBalance());
                break;
            case "MANUAL_FREEZE":
                logger.info("Account {}  manually frozen", event.getAccountId());
                break;
            case "ACCOUNT_UNFROZEN":
                logger.info("Account {} unfrozen", event.getAccountId());
                break;
            default:
                logger.info("Received event: {}", event.getEventType());
        }
    }
}
