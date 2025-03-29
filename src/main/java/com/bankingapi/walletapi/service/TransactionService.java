package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.TransferRequest;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.enums.TransactionType;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bankingapi.walletapi.dto.TransactionResponse;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public TransactionResponse getTransactionResponseById(Long id) {
        Transaction transaction = getTransactionById(id);
        return mapToDTO(transaction);
    }

    public List<TransactionResponse> transferFunds(TransferRequest request) {
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

        logger.info("Transfer completed. Sender new balance: {}, Receiver new balance: {}", sender.getBalance(), receiver.getBalance());

        Transaction debitTransaction = new Transaction();
        debitTransaction.setSenderAccount(sender);
        debitTransaction.setReceiverAccount(receiver);
        debitTransaction.setAmount(amount);
        debitTransaction.setTransactionType(TransactionType.DEBIT);
        debitTransaction.setDescription("Transfer from account #" + receiver.getAccountNumber());
        debitTransaction.setCreatedAt(LocalDateTime.now());

        Transaction creditTransaction = new Transaction();
        creditTransaction.setSenderAccount(sender);
        creditTransaction.setReceiverAccount(receiver);
        creditTransaction.setAmount(amount);
        creditTransaction.setTransactionType(TransactionType.CREDIT);
        creditTransaction.setDescription("Transfer to account #" + sender.getAccountNumber());
        creditTransaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        logger.info("Created 2 transaction records: DEBIT and CREDIT");

        return List.of(mapToDTO(debitTransaction), mapToDTO(creditTransaction));
    }

        public TransactionResponse mapToDTO (Transaction transaction){
            TransactionResponse dto = new TransactionResponse();
            dto.setTransactionId(transaction.getId());
            dto.setAmount(transaction.getAmount());
            dto.setDescription(transaction.getDescription());
            dto.setTransactionType(transaction.getTransactionType());
            dto.setCreatedAt(transaction.getCreatedAt());

            if (transaction.getSenderAccount() != null) {
                dto.setSenderId(transaction.getSenderAccount().getId());
            }

            if (transaction.getReceiverAccount() != null) {
                dto.setReceiverId(transaction.getReceiverAccount().getId());
            }

            if (transaction.getTransactionType() == TransactionType.DEBIT) {
                if (transaction.getSenderAccount() != null) {
                    dto.setAccountId(transaction.getSenderAccount().getId());
                }
            } else if (transaction.getTransactionType() == TransactionType.CREDIT) {
                if (transaction.getReceiverAccount() != null) {
                    dto.setAccountId(transaction.getReceiverAccount().getId());
                }
            }
            return dto;
        }

        public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
            List<Transaction> sent = transactionRepository.findBySenderAccount_Id(accountId);
            List<Transaction> received = transactionRepository.findByReceiverAccount_Id(accountId);

            List<TransactionResponse> allTransactions = new ArrayList<>();

            for (Transaction t : sent) {
                TransactionResponse dto = mapToDTO(t);
                allTransactions.add(dto);
            }

            for (Transaction t : received) {
                TransactionResponse dto = mapToDTO(t);
                allTransactions.add(dto);
            }
            return allTransactions;
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

        public void exportTransactionsToCsv(PrintWriter writer) {
           List<Transaction> transactions = transactionRepository.findAll();

           writer.println("Transaction ID, Sender ID, Receiver ID, Amount, Description,CreatedAt");
           for (Transaction t : transactions) {
               writer.println(String.format("%d,%s,%s,%.2f,%s,%s",
                       t.getId(),
                       t.getSenderAccount() != null ? t.getSenderAccount().getId() : "N/A",
                       t.getReceiverAccount() != null ? t.getReceiverAccount().getId() : "N/A",
                       t.getAmount(),
                       t.getDescription(),
                       t.getCreatedAt().toString()
               ));
           }
        }
}
