package com.kanna.banco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoney {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;

}
