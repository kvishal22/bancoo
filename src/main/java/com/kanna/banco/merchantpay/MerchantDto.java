package com.kanna.banco.merchantpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantDto {

    @NotEmpty
    private String merchantName;
    @NotEmpty
    private String accountNumber;
    @NotEmpty
    private String password;
    @NotEmpty
    private BigDecimal amount;
    @NotEmpty
    private String email;

}
