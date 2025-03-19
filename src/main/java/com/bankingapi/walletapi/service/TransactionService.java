package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.TransferRequest;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.bankingapi.walletapi.dto.TransactionResponse;

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

    public TransactionResponse transferFunds(TransferRequest request) {
        BankAccount sender = bankAccountRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender account not found."));

        BankAccount receiver = bankAccountRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver account not found."));

        BigDecimal amount = request.getAmount();
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
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDTO(savedTransaction);

    }

        public TransactionResponse mapToDTO (Transaction transaction){
            TransactionResponse dto = new TransactionResponse();
            dto.setTransactionId(transaction.getId());
            dto.setSenderId(transaction.getSenderAccount().getId());
            dto.setReceiverId(transaction.getReceiverAccount().getId());
            dto.setAmount(transaction.getAmount());
            dto.setDescription(transaction.getDescription());
            dto.setCreatedAt(transaction.getCreatedAt());
            return dto;
        }


}
