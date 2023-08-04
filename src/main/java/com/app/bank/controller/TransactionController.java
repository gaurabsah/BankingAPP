package com.app.bank.controller;

import com.app.bank.model.Transaction;
import com.app.bank.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/bankStatement")
public class TransactionController {

    @Autowired
    private BankStatement bankStatement;

    @GetMapping
    private List<Transaction> generateStatement(@RequestParam String accountNumber,
                                                @RequestParam String startDate,
                                                @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        List<Transaction> transactions = bankStatement.generateTransactions(accountNumber, startDate, endDate);
        return transactions;
    }
}
