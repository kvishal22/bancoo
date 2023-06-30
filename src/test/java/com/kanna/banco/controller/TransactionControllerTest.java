package com.kanna.banco.controller;

import com.kanna.banco.statement.TransactionController;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.statement.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 class TransactionControllerTest {
    @InjectMocks
    private TransactionController transactionController;
    @Mock
    private TransactionService transactionService;
    private MockMvc mockMvc;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        transactionController = new TransactionController(transactionService);
        mockMvc= MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void getTransactionsByAccountNumber() throws Exception {
        String accountNumber = "23";
        Transactions transactions1 = new Transactions(1,"23","debit",
                BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2), LocalDateTime.now());


        when(transactionService.getTransactionsByAccountNumber(accountNumber))
                .thenReturn(new ArrayList<Transactions>(Collections.singleton(transactions1)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/notanuser/transaction/23")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"23\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void getByCredits() throws Exception {
            String accountNumber = "123456";
            List<Transactions> transactionsList = new ArrayList<>();
            Transactions transactions1 = new Transactions(1,"123456","debit",
                    BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
            Transactions transactions2 = new Transactions(1,"123456","credit",
                    BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
            transactionsList.add(transactions2);
            transactionsList.add(transactions1);
            when(transactionService.getCredits(accountNumber))
                    .thenReturn(transactionsList);
            //verify(transactionService,times(1)).getCredits(accountNumber);

       mockMvc.perform(MockMvcRequestBuilders.get("/api/notanuser/transaction/credits/123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"123456\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void getByDebits() throws Exception {
        String accountNumber = "123456";
        List<Transactions> transactionsList = new ArrayList<>();
        Transactions transactions1 = new Transactions(1,"123456","debit",
                BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
        Transactions transactions2 = new Transactions(1,"123456","credit",
                BigDecimal.valueOf(1),"success",BigDecimal.valueOf(2),LocalDateTime.now());
        transactionsList.add(transactions2);
        transactionsList.add(transactions1);
        when(transactionService.getDebits(accountNumber))
                .thenReturn(transactionsList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/notanuser/transaction/debits/123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"123456\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}

