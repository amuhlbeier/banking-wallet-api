package com.bankingapi.walletapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for creating a new bank account")
public class BankAccountRequest {

    @Schema(
            description = "Unique account number assigned to the bank account",
            example = "1234567890"
    )
    @NotNull(message = "Account number is required")
    private String accountNumber;


    @Schema(
            description = "Type of the bank account",
            example = "SAVINGS"
    )
    @NotNull(message = "Account type is required")
    private String accountType;


    @Schema(
            description = "ID of the user who owns the account",
            example = "101"
    )
    @NotNull(message = "User ID is required")
    private Long userId;

}
