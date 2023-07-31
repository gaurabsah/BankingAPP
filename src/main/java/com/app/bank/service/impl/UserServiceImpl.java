package com.app.bank.service.impl;

import com.app.bank.dto.*;
import com.app.bank.model.User;
import com.app.bank.repository.UserRepository;
import com.app.bank.service.EmailService;
import com.app.bank.service.TransactionService;
import com.app.bank.service.UserService;
import com.app.bank.util.AccountUtils;
import com.app.bank.util.BankConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TransactionService transactionService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        Boolean existsByEmail = userRepository.existsByEmail(userRequest.getEmail());
        if (existsByEmail) {
            return BankResponse.builder()
                    .responseCode(BankConstants.ACCOUNT_ALREADY_EXIST)
                    .responseMessage(BankConstants.ACCOUNT_ALREADY_EXIST_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userRequest.getFirstName() + " " + userRequest.getLastName())
                            .build())
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
                        "Account Balance: " + savedUser.getAccountBalance() + "\n" +
                        "\n" +
                        "Thanks & Regards")
//                .attachments()
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(BankConstants.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(BankConstants.ACCOUNT_CREATION_MESSAGE)
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
                .responseMessage(BankConstants.ACCOUNT_EXISTS_CODE)
                .responseCode(BankConstants.ACCOUNT_EXISTS_MESSAGE)
                .build();
    }

    // balance Enquiry, name Enquiry, credit, debit, transfer

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(BankConstants.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(BankConstants.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(BankConstants.ACCOUNT_FOUND_CODE)
                .responseMessage(BankConstants.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankConstants.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(BankConstants.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(BankConstants.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

//        save transaction
        TransactionDto tx = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("Credit")
                .amount(request.getAmount())
                .status("")
                .build();

        transactionService.saveTransaction(tx);

        return BankResponse.builder()
                .responseCode(BankConstants.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(BankConstants.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(BankConstants.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(BankConstants.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(BankConstants.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(BankConstants.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            //        save transaction
            TransactionDto tx = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("Debit")
                    .amount(request.getAmount())
                    .status("")
                    .build();

            transactionService.saveTransaction(tx);

            return BankResponse.builder()
                    .responseCode(BankConstants.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(BankConstants.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }

    }

    @Override
    public BankResponse transfer(TransferRequest request) {
//        get the account to debit(check)
//        check if the amount debiting is not more than current account balance
//        debit the account
//        get the account to credit (check)
//        credit the account


        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(BankConstants.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(BankConstants.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) < 0) {
            return BankResponse.builder()
                    .responseCode(BankConstants.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(BankConstants.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);

        //        save transaction
        TransactionDto tx = TransactionDto.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType("Transfer/Credit")
                .amount(request.getAmount())
                .status("")
                .build();

        transactionService.saveTransaction(tx);

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .message(request.getAmount() + " has been debited from your account. Your updated account balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        //        save transaction
        TransactionDto tx1 = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("Transfer/Credit")
                .amount(request.getAmount())
                .status("")
                .build();

        transactionService.saveTransaction(tx1);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .message(request.getAmount() + " has been credited to your account. Your updated account balance is " + destinationAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);


        return BankResponse.builder()
                .responseCode(BankConstants.TRANSFER_SUCCESS)
                .responseMessage(BankConstants.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }

}
