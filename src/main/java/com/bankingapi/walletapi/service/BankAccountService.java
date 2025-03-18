package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }
    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }
    public BankAccount createAccount(BankAccount account) {
        return bankAccountRepository.save(account);
    }
    public BankAccount getAccountById(Long id) {
        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank account with id " + id + " not found"));
    }
    public void deleteAccount(Long id) {
        bankAccountRepository.deleteById(id);
    }
}
