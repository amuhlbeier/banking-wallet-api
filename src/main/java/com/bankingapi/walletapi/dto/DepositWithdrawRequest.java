package com.bankingapi.walletapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositWithdrawRequest {

    @NotNull
    private BigDecimal amount;

}
