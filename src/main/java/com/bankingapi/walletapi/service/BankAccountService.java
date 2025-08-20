package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.BankAccountRequest;
import com.bankingapi.walletapi.dto.BankAccountResponse;
import com.bankingapi.walletapi.dto.DepositWithdrawRequest;
import com.bankingapi.walletapi.enums.TransactionType;
import com.bankingapi.walletapi.exception.AccountFrozenException;
import com.bankingapi.walletapi.exception.InsufficientFundsException;
import com.bankingapi.walletapi.exception.ResourceNotFoundException;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.repository.TransactionRepository;
import com.bankingapi.walletapi.model.User;
import com.bankingapi.walletapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

    private static final BigDecimal MIN_BALANCE = new BigDecimal("50.00");
    private static final BigDecimal MAX_OVERDRAFT = new BigDecimal("-100.00");

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<BankAccountResponse> getAllAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        Random random = new Random();

        do {
            accountNumber = String.format("%09d", random.nextInt(1_000_000_000));
        } while (bankAccountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public BankAccountResponse createAccount(BankAccountRequest request) {
        logger.info("Creating a new bank account for user ID: {}", request.getUserId());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        BankAccount account = new BankAccount();
        account.setAccountNumber(generateUniqueAccountNumber());
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
                .orElseThrow(() -> new ResourceNotFoundException("Bank account with id " + id + " not found"));
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
               .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

       if (account.isFrozen()) {
           throw new AccountFrozenException("Account is frozen. Deposit denied.");
       }

        account.setBalance(account.getBalance().add(request.getAmount()));
        BankAccount updated = bankAccountRepository.save(account);

       Transaction transaction = new Transaction();
       transaction.setSenderAccount(null);
       transaction.setReceiverAccount(account);
       transaction.setAmount(request.getAmount());
       transaction.setTransactionType(TransactionType.CREDIT);
       transaction.setDescription("Deposit to account #" + account.getAccountNumber());
       transaction.setCreatedAt(LocalDateTime.now());
       transactionRepository.save(transaction);

        logger.info("Deposit successful. New balance for account ID {}: {}", accountId, updated.getBalance());
        return mapToDTO(updated);
   }

   public BankAccountResponse withdrawFunds(Long accountId, DepositWithdrawRequest request){
        logger.info("Withdrawing {} out of account ID: {}", request.getAmount(), accountId);

       BankAccount account = bankAccountRepository.findById(accountId)
               .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        if (account.isFrozen()) {
            throw new AccountFrozenException("Account is frozen. Withdrawal denied.");
        }

        BigDecimal projectedBalance = account.getBalance().subtract(request.getAmount());

        if (projectedBalance.compareTo(MAX_OVERDRAFT) < 0) {
            throw new InsufficientFundsException("Overdraft limit exceeded. Withdrawal denied.");
        }

        if (projectedBalance.compareTo(MIN_BALANCE) < 0 && projectedBalance.compareTo(BigDecimal.ZERO) >= 0) {
            logger.warn("Balance alert for account {}", accountId);
        }

       if (projectedBalance.compareTo(BigDecimal.ZERO) < 0 && projectedBalance.compareTo(MAX_OVERDRAFT) <= 0) {
           account.setFrozen(true);
           logger.info("Account {} auto-frozen due to overdraft.", accountId);
       }

        account.setBalance(projectedBalance);
        BankAccount updated = bankAccountRepository.save(account);

       Transaction transaction = new Transaction();
       transaction.setSenderAccount(account);
       transaction.setReceiverAccount(null);
       transaction.setAmount(request.getAmount());
       transaction.setTransactionType(TransactionType.DEBIT);
       transaction.setDescription("Withdrawal from account #" + account.getAccountNumber());
       transaction.setCreatedAt(LocalDateTime.now());
       transactionRepository.save(transaction);

        return mapToDTO (updated);
   }

   public void freezeAccount(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account with id " + accountId + " not found"));
        account.setFrozen(true);
        bankAccountRepository.save(account);
   }

   public void unfreezeAccount(Long accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account with id " + accountId + " not found"));
        account.setFrozen(false);
        bankAccountRepository.save(account);
   }

    public BankAccountResponse mapToDTO(BankAccount account) {
        BankAccountResponse dto = new BankAccountResponse();
        dto.setAccountId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setUserId(account.getUser().getId());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setFrozen(account.isFrozen());
        return dto;
    }

}
