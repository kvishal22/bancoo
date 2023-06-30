package com.kanna.banco.service;

import com.kanna.banco.statement.TransactionDto;
import com.kanna.banco.statement.TransactionRepo;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.statement.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

 class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    private TransactionService transactionService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(transactionRepo);
    }

    @Test
     void saveTransaction() {

        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("credit")
                .accountNumber("82374849")
                .amount(BigDecimal.valueOf(100))
                .balanceAfterTransaction(BigDecimal.valueOf(200))
                .build();
        transactionService.saveTransaction(transactionDto);

        verify(transactionRepo,times(1)).save(any(Transactions.class));
    }
    @Test
    void getTransactionsByAccountNumber(){
        String accountNumber = "123456";
           Transactions transactions1 = new Transactions(1,"123456","debit",
                    BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());


        Mockito.when(transactionRepo.findByAccountNumber(accountNumber)).thenReturn(
        new ArrayList<Transactions>(Collections.singleton(transactions1)));
        assertThat(transactionService.getTransactionsByAccountNumber(accountNumber).get(0).getAccountNumber())
                .isEqualTo(transactions1.getAccountNumber());
        verify(transactionRepo,times(1)).findByAccountNumber(accountNumber);

    }
    @Test
    void getTransactionsByCredit(){
        String accountNumber = "123456";
        List<Transactions> transactionsList = new ArrayList<>();
        Transactions transactions1 = new Transactions(1,"123456","debit",
                BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
        Transactions transactions2 = new Transactions(1,"123456","credit",
                BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
        transactionsList.add(transactions2);
        transactionsList.add(transactions1);

        Mockito.when(transactionRepo.findByAccountNumberAndTransactionType(accountNumber,"credit"))
                .thenReturn(transactionsList);
        assertThat(transactionService.getCredits(accountNumber).get(0).getAmount())
                .isEqualTo(transactions1.getAmount());
        verify(transactionRepo,times(1))
                .findByAccountNumberAndTransactionType(accountNumber,"credit");
    }
    @Test
    void getTransactionsByDebit(){
        String accountNumber = "123456";
        List<Transactions> transactionsList = new ArrayList<>();
        Transactions transactions1 = new Transactions(1,"123456","debit",
                BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
        Transactions transactions2 = new Transactions(1,"123456","credit",
                BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
        transactionsList.add(transactions2);
        transactionsList.add(transactions1);

        Mockito.when(transactionRepo.findByAccountNumberAndTransactionType(accountNumber,"debit"))
                .thenReturn(transactionsList);
        assertThat(transactionService.getDebits(accountNumber).get(0).getAmount())
                .isEqualTo(transactions1.getAmount());
        verify(transactionRepo,times(1))
                .findByAccountNumberAndTransactionType(accountNumber,"debit");
    }
}