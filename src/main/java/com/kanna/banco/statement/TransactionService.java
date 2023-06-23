package com.kanna.banco.statement;

import com.kanna.banco.dto.EnquiryReq;
import com.kanna.banco.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepo repo;
    public void saveTransaction(TransactionDto transactionDto) {

        Transactions transactions = Transactions.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("success")
                .transactionTime(LocalDateTime.now())
                .balanceAfterTransaction(transactionDto.getBalanceAfterTransaction())
                .build();
        repo.save(transactions);
    }
    public List<Transactions> getTransactionsByAccountNumber(String accountNumber) {
        return repo.findByAccountNumber(accountNumber);
    }
    public List<Transactions> getCredits(String accountNumber){
        return repo.findByAccountNumberAndTransactionType(accountNumber, "credit");
    }

    public List<Transactions> getDebits(String accountNumber) {
        return repo.findByAccountNumberAndTransactionType(accountNumber, "debit");
    }
}

