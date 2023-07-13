package com.kanna.banco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDebitReq {
    @NotEmpty
    private String accountNumber;
    @NotEmpty
    private BigDecimal amount;
    @NotEmpty
    private String password;
}
