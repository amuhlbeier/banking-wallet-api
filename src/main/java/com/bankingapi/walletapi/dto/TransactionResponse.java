package com.bankingapi.walletapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}
