package com.app.bank.service.impl;

import com.app.bank.dto.AccountInfo;
import com.app.bank.dto.BankResponse;
import com.app.bank.dto.EmailDetails;
import com.app.bank.dto.UserRequest;
import com.app.bank.model.User;
import com.app.bank.repository.UserRepository;
import com.app.bank.service.EmailService;
import com.app.bank.service.UserService;
import com.app.bank.util.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .email(userRequest.getEmail())
                .address(userRequest.getAddress())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .gender(userRequest.getGender())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();
        User savedUser = userRepository.save(newUser);

//        send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Created...")
                .message("Congratulations " + savedUser.getFirstName() + ", Your Account has been created successfully.\nYour Account Details:\n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n" +
                        "Account Number: " + savedUser.getAccountNumber().toString() + "\n" +
                        "Account Balance: " + savedUser.getAccountBalance()+"\n" +
                        "\n" +
                        "Thanks & Regards")
//                .attachments()
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber().toString())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();

    }

    @Override
    public BankResponse getAccountDetails(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return BankResponse.builder()
                .accountInfo(AccountInfo.builder()
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getAccountBalance())
                        .accountNumber(user.getAccountNumber().toString())
                        .build())
                .responseMessage(AccountUtils.ACCOUNT_FETCH_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_FETCH_CODE)
                .build();
    }
}
