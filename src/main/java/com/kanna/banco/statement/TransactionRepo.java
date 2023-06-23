package com.kanna.banco.statement;

import com.kanna.banco.dto.EnquiryReq;
import com.kanna.banco.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transactions, Integer> {


   List<Transactions> findByAccountNumber(String accountNumber);
   List<Transactions> findByAccountNumberAndTransactionType(String accountNumber, String transactionType);

}
