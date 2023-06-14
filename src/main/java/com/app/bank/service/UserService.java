package com.app.bank.service;

import com.app.bank.dto.BankResponse;
import com.app.bank.dto.UserRequest;
import com.app.bank.model.User;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse getAccountDetails(Long userId);
}
