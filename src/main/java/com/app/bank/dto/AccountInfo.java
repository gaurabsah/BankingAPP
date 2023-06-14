package com.app.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountInfo {

    private String accountName;
    private String accountNumber;
    private BigDecimal accountBalance;
}
