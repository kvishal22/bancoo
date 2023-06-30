package com.kanna.banco.statement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ComponentScan
@Component
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String accountNumber;
    private String transactionType;
    private BigDecimal amount;
    private String status;
    private BigDecimal balanceAfterTransaction;

    @CreationTimestamp
    private LocalDateTime transactionTime;


}
