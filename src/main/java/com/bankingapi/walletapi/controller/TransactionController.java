package com.bankingapi.walletapi.controller;

import com.bankingapi.walletapi.dto.TransferRequest;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import com.bankingapi.walletapi.dto.TransactionResponse;
import com.bankingapi.walletapi.dto.TransferRequest;

import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferFunds(@RequestBody @Valid TransferRequest request) {
        TransactionResponse result = transactionService.transferFunds(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<TransactionResponse> history = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(history);
    }
}
