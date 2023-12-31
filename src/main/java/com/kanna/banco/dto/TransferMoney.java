package com.kanna.banco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferMoney {

    @NotEmpty
    private String fromAccountNumber;
    @NotEmpty
    private String toAccountNumber;
    @NotEmpty
    private BigDecimal amount;
    @NotEmpty
    private String password;
    //new
    private LocalDateTime scheduledTime;

}
