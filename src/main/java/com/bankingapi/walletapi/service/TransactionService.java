package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.TransferRequest;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bankingapi.walletapi.dto.TransactionResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

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
        logger.info("Initiating transfer of amount: {}", request.getAmount());
        BankAccount sender = bankAccountRepository.findById(request.getSenderId())
                .orElseThrow(() -> {
                    logger.error("Sender account with ID {} not found", request.getSenderId());
                    return new RuntimeException("Sender account not found.");
                });

        BankAccount receiver = bankAccountRepository.findById(request.getReceiverId())
                .orElseThrow(() -> {
                    logger.error("Receiver account with ID {} not found.", request.getReceiverId());
                    return new RuntimeException("Receiver account not found.");
                });

        BigDecimal amount = request.getAmount();
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in sender account.");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        bankAccountRepository.save(receiver);
        bankAccountRepository.save(sender);

        logger.info("Transfer completed. Sender new balance: {}", sender.getBalance());

        Transaction transaction = new Transaction();
        transaction.setSenderAccount(sender);
        transaction.setReceiverAccount(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction ID: {}", savedTransaction.getId());

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

        public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
            List<Transaction> sent = transactionRepository.findBySenderAccount_Id(accountId);
            List<Transaction> received = transactionRepository.findByReceiverAccount_Id(accountId);

            List<Transaction> allTransactions = new ArrayList<>();
            allTransactions.addAll(sent);
            allTransactions.addAll(received);

            return allTransactions.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        public List<TransactionResponse> getTransactionsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
            List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(fromDate,toDate);
            return transactions.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        public List<TransactionResponse> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
           List<Transaction> transactions = transactionRepository.findByAmountBetween(minAmount, maxAmount);
           return transactions.stream()
                   .map(this::mapToDTO)
                   .collect(Collectors.toList());
        }

        public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
           Page<Transaction> transactions = transactionRepository.findAll(pageable);
           return transactions.map(this::mapToDTO);
        }


}
