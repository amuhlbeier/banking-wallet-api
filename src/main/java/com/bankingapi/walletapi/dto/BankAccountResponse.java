package com.bankingapi.walletapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object containing bank account details")
public class BankAccountResponse {

    @Schema(
            description = "ID of the bank account",
            example = "1"
    )
    private Long accountId;

    @Schema(
            description = "Account Number",
            example = "123456789"
    )
    private String accountNumber;

    @Schema(
            description = "Type of the account",
            example = "Checking"
    )
    private String accountType;

    @Schema(
            description = "User ID that owns this account",
            example = "42"
    )
    private Long userId;

    @Schema(
            description = "Balance of the bank account",
            example = "1000.50"
    )
    private BigDecimal balance;

    @Schema(
            description = "Time bank account was created",
            example = "2025-03-17T14:30:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Indicates whether the account is frozen",
            example = "false")
    private boolean frozen;
}
