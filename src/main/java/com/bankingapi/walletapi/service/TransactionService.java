package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found."));
    }

    public Transaction transferFunds(Long senderAccountId, Long receiverAccountId, BigDecimal amount, String description) {
        BankAccount sender = bankAccountRepository.findById(senderAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found."));

        BankAccount receiver = bankAccountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found."));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in sender account.");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        bankAccountRepository.save(receiver);
        bankAccountRepository.save(sender);

        Transaction transaction = new Transaction();
        transaction.setSenderAccount(sender);
        transaction.setReceiverAccount(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }


}
