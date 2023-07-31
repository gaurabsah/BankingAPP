package com.app.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionDto {
    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
}
