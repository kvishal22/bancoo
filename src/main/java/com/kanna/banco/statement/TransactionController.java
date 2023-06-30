package com.kanna.banco.statement;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/notanuser")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/transaction/{accountNumber}")
    public List<Transactions> getTransactionsByAccountNumber(@PathVariable String accountNumber) {
        return transactionService.getTransactionsByAccountNumber(accountNumber);
    }
    @GetMapping("/transaction/credits/{accountNumber}")
    public List<Transactions> getCredits(@PathVariable String accountNumber){
        return transactionService.getCredits(accountNumber);
    }
    @GetMapping("/transaction/debits/{accountNumber}")
    public List<Transactions> getDebits(@PathVariable String accountNumber){
        return transactionService.getDebits(accountNumber);
    }
    }

