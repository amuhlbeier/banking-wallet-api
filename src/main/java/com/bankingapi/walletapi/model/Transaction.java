package com.bankingapi.walletapi.model;

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
    @JoinColumn(name = "sender_account_id", nullable = false)
    private BankAccount senderAccount;

    @ManyToOne
    @JoinColumn(name = "reciever_account_id", nullable = false)
    private BankAccount recieverAccount;

}
