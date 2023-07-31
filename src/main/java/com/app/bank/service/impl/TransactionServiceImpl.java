package com.app.bank.service.impl;

import com.app.bank.dto.TransactionDto;
import com.app.bank.model.Transaction;
import com.app.bank.repository.TransactionRepository;
import com.app.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transaction) {
        Transaction tx = Transaction.builder()
                .transactionType(transaction.getTransactionType())
                .accountNumber(transaction.getAccountNumber())
                .amount(transaction.getAmount())
                .status("Succeeded")
                .build();
        transactionRepository.save(tx);
    }
}
