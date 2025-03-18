package com.bankingapi.walletapi.controller;

import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Transaction> transferFunds(
            @RequestParam @NotNull Long senderId,
            @RequestParam @NotNull Long receiverId,
            @RequestParam @NotNull BigDecimal amount,
            @RequestParam(required = false) String description
    ) {
        Transaction result = transactionService.transferFunds(senderId, receiverId, amount, description);
        return ResponseEntity.ok(result);
    }

}
