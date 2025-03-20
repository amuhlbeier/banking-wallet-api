package com.bankingapi.walletapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @Schema(
            description = "ID of the sender",
            example = "1"
    )
    @NotNull
    private Long senderId;


    @Schema(
            description = "ID of the receiver",
            example = "2"
    )
    @NotNull
    private Long receiverId;


    @Schema(
            description = "Amount of the transaction",
            example = "750.00"
    )
    @NotNull
    private BigDecimal amount;


    @Schema(
            description = "Description of the transaction",
            example = "Rent payment"
    )
    private String description;

}
