package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.BankAccountRequest;
import com.bankingapi.walletapi.dto.BankAccountResponse;
import com.bankingapi.walletapi.dto.DepositWithdrawRequest;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

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
        User user = new User();
        user.setId(request.getUserId());

        BankAccount account = new BankAccount();
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setUser(user);
        account.setCreatedAt(LocalDateTime.now());
        account.setBalance(BigDecimal.ZERO);

        BankAccount saved = bankAccountRepository.save(account);
        return mapToDTO(saved);
    }
    public BankAccountResponse getAccountById(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account with id " + id + " not found"));
        return mapToDTO(account);
    }
    public void deleteAccount(Long id) {
        bankAccountRepository.deleteById(id);
    }
   public BankAccountResponse depositFunds(Long accountId, DepositWithdrawRequest request){
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(request.getAmount()));
        return mapToDTO(bankAccountRepository.save(account));
   }
   public BankAccountResponse withdrawFunds(Long accountId, DepositWithdrawRequest request){
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        return mapToDTO (bankAccountRepository.save(account));
   }

    public BankAccountResponse mapToDTO(BankAccount account) {
        BankAccountResponse dto = new BankAccountResponse();
        dto.setAccountId(account.getId());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }

}
