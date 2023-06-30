package com.kanna.banco.statement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions, Integer> {

   List<Transactions> findByAccountNumber(String accountNumber);
   List<Transactions> findByAccountNumberAndTransactionType(String accountNumber, String transactionType);

}
