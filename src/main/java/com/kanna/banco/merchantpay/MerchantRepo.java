package com.kanna.banco.merchantpay;

import org.springframework.data.jpa.repository.JpaRepository;


public interface MerchantRepo extends JpaRepository<MerchantPay,Integer> {
    MerchantPay findByEmail(String email);
    MerchantPay findByEmailAndOtp(String email,String otp);
    MerchantPay findByOtp(String otp);
}
