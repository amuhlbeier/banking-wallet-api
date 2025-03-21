package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.BankAccountRequest;
import com.bankingapi.walletapi.dto.BankAccountResponse;
import com.bankingapi.walletapi.dto.DepositWithdrawRequest;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.model.User;
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
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

    private static final BigDecimal MIN_BALANCE = new BigDecimal("50.00");
    private static final BigDecimal MAX_OVERDRAFT = new BigDecimal("-100.00");

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.bankAccountRepository = bankAccountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<BankAccountResponse> getAllAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public BankAccountResponse createAccount(BankAccountRequest request) {
        logger.info("Creating a new bank account for user ID: {}", request.getUserId());
        User user = new User();
        user.setId(request.getUserId());

        BankAccount account = new BankAccount();
        account.setAccountNumber(request.getAccountNumber());
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
                .orElseThrow(() -> {
                    logger.error("Bank account with id {} not found", accountId);
                    return new RuntimeException("Account not found");
                });

        if (account.isFrozen()) {
            throw new RuntimeException("Account is frozen. Deposit denied.");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        BankAccount updated = bankAccountRepository.save(account);
        kafkaTemplate.send("account-events", "DEPOSIT: $" + request.getAmount() + " to account " + accountId);
        logger.info("Deposit successful. New balance for account ID {}: {}", accountId, updated.getBalance());
        return mapToDTO(updated);
   }

   public BankAccountResponse withdrawFunds(Long accountId, DepositWithdrawRequest request){
        logger.info("Withdrawing {} out of account ID: {}", request.getAmount(), accountId);
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.error("Bank account with id {} not found", accountId);
                    return new RuntimeException("Account not found");
                });

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        if (account.isFrozen()) {
            throw new RuntimeException("Account is frozen. Withdrawal denied.");
        }

        BigDecimal projectedBalance = account.getBalance().subtract(request.getAmount());

        if (projectedBalance.compareTo(MAX_OVERDRAFT) < 0) {
            kafkaTemplate.send("account-events", "OVERDRAFT_EXCEEDED: Account " + accountId);
            throw new RuntimeException("Overdraft limit exceeded. Withdrawal denied.");
        }

        if (projectedBalance.compareTo(MIN_BALANCE) < 0 && projectedBalance.compareTo(BigDecimal.ZERO) >= 0) {
            kafkaTemplate.send("account-events", "BALANCE_ALERT: Account " + accountId);
        }

       if (projectedBalance.compareTo(BigDecimal.ZERO) < 0 && projectedBalance.compareTo(MAX_OVERDRAFT) <= 0) {
           account.setFrozen(true);
           kafkaTemplate.send("account-events", "ACCOUNT_FROZEN: Account " + accountId);
           logger.info("Account {} auto-frozen due to overdraft.", accountId);
       }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        BankAccount updated = bankAccountRepository.save(account);
       kafkaTemplate.send("account-events", "WITHDRAWAL: $" + request.getAmount() + " to account " + accountId);
        logger.info("Withdrawal successful. New balance for account ID {}: {}", accountId, updated.getBalance());
        return mapToDTO (updated);
   }

   public void freezeAccount(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account with id " + accountId + " not found"));
        account.setFrozen(true);
        bankAccountRepository.save(account);
        kafkaTemplate.send("account-events", "MANUAL_FREEZE: Account " + accountId);
   }

   public void unfreezeAccount(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account with id " + accountId + " not found"));
        account.setFrozen(false);
        bankAccountRepository.save(account);
        kafkaTemplate.send("account-events", "ACCOUNT_UNFROZEN: Account " + accountId);
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
