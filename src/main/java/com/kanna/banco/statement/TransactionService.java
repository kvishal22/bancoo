package com.kanna.banco.statement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
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

