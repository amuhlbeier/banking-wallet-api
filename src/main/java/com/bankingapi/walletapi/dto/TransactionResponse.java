package com.bankingapi.walletapi.dto;

import com.bankingapi.walletapi.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing transaction details")
public class TransactionResponse {

    @Schema(
            description = "ID of the transaction",
            example = "1001"
    )
    private Long transactionId;

    @Schema(
            description = "ID of the sender",
            example = "1"
    )
    private Long senderId;

    @Schema(
            description = "ID of the receiver",
            example = "2"
    )
    private Long receiverId;

    @Schema(
            description = "Amount of the transaction",
            example = "500.00"
    )
    private BigDecimal amount;

    @Schema(
            description = "Description of the transaction",
            example = "Payment for rent"
    )
    private String description;

    @Schema(
            description = "Time the transaction was created",
            example = "2025-03-19T15:30:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Account ID of viewer of the transaction",
            example = "1"
    )
    private Long accountId;

    @Schema(
            description = "Type of transaction",
            example = "Debit"
    )
    private TransactionType transactionType;

}
