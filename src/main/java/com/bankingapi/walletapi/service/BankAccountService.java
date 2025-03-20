package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.BankAccountRequest;
import com.bankingapi.walletapi.dto.BankAccountResponse;
import com.bankingapi.walletapi.dto.DepositWithdrawRequest;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
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

        account.setBalance(account.getBalance().add(request.getAmount()));
        BankAccount updated = bankAccountRepository.save(account);
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

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        BankAccount updated = bankAccountRepository.save(account);
        logger.info("Withdrawal successful. New balance for account ID {}: {}", accountId, updated.getBalance());
        return mapToDTO (updated);
   }

    public BankAccountResponse mapToDTO(BankAccount account) {
        BankAccountResponse dto = new BankAccountResponse();
        dto.setAccountId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }

}
