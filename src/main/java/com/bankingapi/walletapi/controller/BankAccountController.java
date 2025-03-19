package com.bankingapi.walletapi.controller;
import com.bankingapi.walletapi.dto.BankAccountResponse;
import com.bankingapi.walletapi.dto.DepositWithdrawRequest;
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
    public ResponseEntity<List<BankAccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody BankAccount account) {
       BankAccountResponse createdAccount = bankAccountService.createAccount(account);
       return ResponseEntity.ok(createdAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<BankAccountResponse> deposit(
            @PathVariable Long id,
            @RequestBody @Valid DepositWithdrawRequest request
    ) {
        return ResponseEntity.ok(bankAccountService.depositFunds(id, request));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<BankAccountResponse> withdraw(
            @PathVariable Long id,
            @RequestBody @Valid DepositWithdrawRequest request
    ) {
        return ResponseEntity.ok(bankAccountService.withdrawFunds(id, request));
    }



}
