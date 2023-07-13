package com.kanna.banco.merchantpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantPay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String merhcantName;
    private String accountNumber;
    private String email;
    private String otp;
    private BigDecimal amount;
    private boolean isSuccessful;
    private String password;
    private LocalDateTime otpGeneratedTime;

}
