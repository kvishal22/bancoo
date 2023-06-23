package com.kanna.banco.statement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ComponentScan
@Component
public class TransactionDto {

    private String accountNumber;
    private String transactionType;
    private BigDecimal amount;
    private String status;
    private BigDecimal balanceAfterTransaction;
}
