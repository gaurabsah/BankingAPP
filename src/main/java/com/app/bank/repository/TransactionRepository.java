package com.app.bank.repository;

import com.app.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
}
