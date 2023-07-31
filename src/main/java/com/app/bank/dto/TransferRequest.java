package com.app.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
}
