package com.bankingapi.walletapi.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountResponse {
    private Long accountId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
