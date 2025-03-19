package com.bankingapi.walletapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountRequest {

    @NotNull(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Balance is required")
    private String accountType;

    @NotNull(message = "User ID is required")
    private Long userId;

}
