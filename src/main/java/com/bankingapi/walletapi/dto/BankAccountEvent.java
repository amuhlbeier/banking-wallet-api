package com.bankingapi.walletapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountEvent {

    private String eventType;
    private Long accountId;
    private BigDecimal balance;
    private LocalDateTime timestamp;
    private String description;
}


