package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.BankAccountEvent;
import com.bankingapi.walletapi.dto.BankAccountRequest;
import com.bankingapi.walletapi.dto.BankAccountResponse;
import com.bankingapi.walletapi.dto.DepositWithdrawRequest;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.model.User;
import com.bankingapi.walletapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, BankAccountEvent> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

    private static final BigDecimal MIN_BALANCE = new BigDecimal("50.00");
    private static final BigDecimal MAX_OVERDRAFT = new BigDecimal("-100.00");

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, KafkaTemplate<String, BankAccountEvent> kafkaTemplate, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
    }

    public List<BankAccountResponse> getAllAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public BankAccountResponse createAccount(BankAccountRequest request) {
        logger.info("Creating a new bank account for user ID: {}", request.getUserId());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));


        BankAccount account = new BankAccount();
        String generatedNumber = "ACCT-" + (int)(Math.random() * 1_000_000);
        account.setAccountNumber(generatedNumber);
        account.setAccountType(request.getAccountType());
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());
        account.setBalance(BigDecimal.ZERO);

        BankAccount saved = bankAccountRepository.save(account);
        logger.info("Bank account successfully created for user ID: {}", request.getUserId());
        return mapToDTO(saved);
    }

    public BankAccountResponse getAccountById(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account with id " + id + " not found"));
        return mapToDTO(account);
    }

    public void deleteAccount(Long id) {
        logger.info("Deleting bank account with id {}", id);
        bankAccountRepository.deleteById(id);
        logger.info("Bank account successfully deleted for id {}", id);
    }

   public BankAccountResponse depositFunds(Long accountId, DepositWithdrawRequest request){
        logger.info("Depositing {} into account ID: {}", request.getAmount(), accountId);

       BankAccount account = bankAccountRepository.findById(accountId)
               .orElseThrow(() -> new RuntimeException("Account not found"));

       if (account.isFrozen()) {
           throw new RuntimeException("Account is frozen. Deposit denied.");
       }

        account.setBalance(account.getBalance().add(request.getAmount()));
        BankAccount updated = bankAccountRepository.save(account);

        BankAccountEvent event = new BankAccountEvent(
                "DEPOSIT",
                accountId,
                account.getBalance(),
                LocalDateTime.now(),
                "Deposit successful"
        );
        kafkaTemplate.send("account-events", event);

        logger.info("Deposit successful. New balance for account ID {}: {}", accountId, updated.getBalance());
        return mapToDTO(updated);
   }

   public BankAccountResponse withdrawFunds(Long accountId, DepositWithdrawRequest request){
        logger.info("Withdrawing {} out of account ID: {}", request.getAmount(), accountId);

       BankAccount account = bankAccountRepository.findById(accountId)
               .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        if (account.isFrozen()) {
            throw new RuntimeException("Account is frozen. Withdrawal denied.");
        }


        BigDecimal projectedBalance = account.getBalance().subtract(request.getAmount());

        if (projectedBalance.compareTo(MAX_OVERDRAFT) < 0) {
            BankAccountEvent overdraftEvent =  new BankAccountEvent(
                    "OVERDRAFT_EXCEEDED",
                    accountId,
                    account.getBalance(),
                    LocalDateTime.now(),
                    "Overdraft limit exceeded"
            );
            kafkaTemplate.send("account-events", overdraftEvent);
            throw new RuntimeException("Overdraft limit exceeded. Withdrawal denied.");
        }

        if (projectedBalance.compareTo(MIN_BALANCE) < 0 && projectedBalance.compareTo(BigDecimal.ZERO) >= 0) {
            BankAccountEvent alertEvent =  new BankAccountEvent(
                    "BALANCE_ALERT",
                    accountId,
                    account.getBalance(),
                    LocalDateTime.now(),
                    "Balance dropped below minimum limit"
            );
            kafkaTemplate.send("account-events", alertEvent);
        }

       if (projectedBalance.compareTo(BigDecimal.ZERO) < 0 && projectedBalance.compareTo(MAX_OVERDRAFT) <= 0) {
           account.setFrozen(true);
           kafkaTemplate.send("account-events", new BankAccountEvent(
                   "ACCOUNT_FROZEN",
                   accountId,
                   account.getBalance(),
                   LocalDateTime.now(),
                   "Account frozen due to overdraft"
           ));
           logger.info("Account {} auto-frozen due to overdraft.", accountId);
       }

        account.setBalance(projectedBalance);
        BankAccount updated = bankAccountRepository.save(account);

       BankAccountEvent withdrawalEvent =  new BankAccountEvent(
               "WITHDRAWAL",
               accountId,
               updated.getBalance(),
               LocalDateTime.now(),
               "Withdrawal successful"
       );
        kafkaTemplate.send("account-events", withdrawalEvent);
        return mapToDTO (updated);
   }

   public void freezeAccount(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account with id " + accountId + " not found"));
        account.setFrozen(true);
        bankAccountRepository.save(account);

        BankAccountEvent event = new BankAccountEvent(
               "MANUAL_FREEZE",
               accountId,
               account.getBalance(),
               LocalDateTime.now(),
               "Account manually frozen"
        );
        kafkaTemplate.send("account-events", event);
   }

   public void unfreezeAccount(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account with id " + accountId + " not found"));
        account.setFrozen(false);
        bankAccountRepository.save(account);

       BankAccountEvent event =  new BankAccountEvent(
               "ACCOUNT_UNFROZEN",
               accountId,
               account.getBalance(),
               LocalDateTime.now(),
               "Account manually unfrozen"
       );
       kafkaTemplate.send("account-events", event);
   }

    public BankAccountResponse mapToDTO(BankAccount account) {
        BankAccountResponse dto = new BankAccountResponse();
        dto.setAccountId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setFrozen(account.isFrozen());
        return dto;
    }

}
