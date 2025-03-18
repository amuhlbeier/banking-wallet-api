package com.bankingapi.walletapi.controller;
import com.bankingapi.walletapi.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.repository.BankAccountRepository;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> getAllAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccount> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<BankAccount> createAccount(@Valid @RequestBody BankAccount account) {
       BankAccount createdAccount = bankAccountService.createAccount(account);
       return ResponseEntity.ok(createdAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }


}
