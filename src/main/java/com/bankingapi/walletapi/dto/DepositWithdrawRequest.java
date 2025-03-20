package com.bankingapi.walletapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositWithdrawRequest {

    @Schema(
            description = "The amount to deposit or withdraw",
            example = "50.00"
    )
    @NotNull
    private BigDecimal amount;

}
