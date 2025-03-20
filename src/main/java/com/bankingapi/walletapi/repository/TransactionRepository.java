package com.bankingapi.walletapi.repository;

import com.bankingapi.walletapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderAccount_Id(Long accountId);
    List<Transaction> findByReceiverAccount_Id(Long accountId);
    List<Transaction> findByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
