package com.bankingapi.walletapi.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull
    private Long senderId;

    @NotNull
    private Long receiverId;

    @NotNull
    private BigDecimal amount;

    private String description;

}
