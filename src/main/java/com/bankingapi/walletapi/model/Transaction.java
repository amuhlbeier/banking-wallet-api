package com.bankingapi.walletapi.model;

import com.bankingapi.walletapi.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private LocalDateTime createdAt;

    private String description;

    @ManyToOne
    @JoinColumn(name = "sender_account_id")
    private BankAccount senderAccount;

    @ManyToOne
    @JoinColumn(name = "receiver_account_id")
    private BankAccount receiverAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable= false)
    private TransactionType transactionType;


}
