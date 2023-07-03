package com.kanna.banco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountInfo {

    private String accountName;
    private BigDecimal accountBalance;
    private String accountNumber;
}
