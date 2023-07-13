package com.kanna.banco.merchantpay;

import com.kanna.banco.dto.*;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.service.Emailservice;
import com.kanna.banco.statement.TransactionDto;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepo merchantRepo;
    private final UserRepo userRepo;
    private final TransactionService transactionService;
    private final Emailservice emailservice;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger= LoggerFactory.getLogger(MerchantService.class);
    public BankResponse payToMerchant(MerchantDto req) throws MessagingException {

        User accountNumber = userRepo.findByAccountNumber(req.getAccountNumber());

        if (Boolean.FALSE.equals(accountNumber.getEmail().equals(req.getEmail())) ||
                !passwordEncoder.matches(req.getPassword(), accountNumber.getPassword())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .build();
        }
        BigDecimal availBal = accountNumber.getAccountBalance();
        BigDecimal amountToCredit = req.getAmount();

        if (availBal.compareTo(amountToCredit) < 0 || amountToCredit.compareTo(BigDecimal.ZERO) <= 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .build();
        }
        String otp = AccountUtils.generateOtp();
        MerchantPay merchantPay = MerchantPay.builder()
                .email(req.getEmail())
                .otp(otp)
                .otpGeneratedTime(LocalDateTime.now())
                .merhcantName(req.getMerchantName())
                .email(req.getEmail())
                .amount(req.getAmount())
                .accountNumber(req.getAccountNumber())
                .build();
        merchantRepo.save(merchantPay);

            emailservice.sendOtpEmail(otp,req.getEmail());

        logger.info("otp has been sent to: {}",req.getEmail());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                .responseMessage(AccountUtils.OTP_GENERATED)
                .build();
    }
    public BankResponse payMerchantVerify(String otp,String email) {
        User fromAccountNumber = userRepo.findByEmail(email).orElseThrow();
        MerchantPay merchantPay = merchantRepo.findByOtp(otp);
        if(Boolean.TRUE.equals(merchantPay.isSuccessful())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.AMOUNT_PAID_ALREADY)
                    .responseMessage(AccountUtils.AMOUNT_DEBITED_ALREADY)
                    .build();
        }

        if (merchantPay.getOtp().equals(otp) && Duration.between(merchantPay.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (3 * 60)){

            BigDecimal availBal = fromAccountNumber.getAccountBalance();
            BigDecimal amountToCredit = merchantPay.getAmount();

        fromAccountNumber.setAccountBalance(availBal.subtract(amountToCredit));
        userRepo.save(fromAccountNumber);

        merchantPay.setSuccessful(true);

        TransactionDto transactionDtoTwo = TransactionDto.builder()
                .accountNumber(fromAccountNumber.getAccountNumber())
                .transactionType("debit")
                .amount(merchantPay.getAmount())
                .balanceAfterTransaction(fromAccountNumber.getAccountBalance())
                .build();
        transactionService.saveTransaction(transactionDtoTwo);

            return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(merchantPay.getAccountNumber())
                        .accountName(fromAccountNumber.getFirstName() + " " + fromAccountNumber.getLastName())
                        .accountBalance(fromAccountNumber.getAccountBalance())
                        .build())
                .build();
    }
        return BankResponse.builder()
                .responseMessage(AccountUtils.OTP_EXPIRED)
                .build();

    }


}
