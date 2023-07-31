package com.app.bank.service;

import com.app.bank.dto.*;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse getAccountDetails(Long userId);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transfer(TransferRequest request);
}
