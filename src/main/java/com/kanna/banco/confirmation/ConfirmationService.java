package com.kanna.banco.confirmation;

import com.kanna.banco.dto.AccountInfo;
import com.kanna.banco.dto.BankResponse;
import com.kanna.banco.dto.EmailDeets;
import com.kanna.banco.dto.UserReq;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.service.Emailservice;
import com.kanna.banco.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class ConfirmationService {

    private final UserRepo userRepo;
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final Emailservice emailservice;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserReq userReq) {

        if (userRepo.existsByEmail(userReq.getEmail()) || userRepo.existsByPhoneNumber(userReq.getPhoneNumber())) {
            return "user already exists";
        }
        User newUser = User.builder()
                .firstName(userReq.getFirstName())
                .lastName(userReq.getLastName())
                .address((userReq.getAddress()))
                .state(userReq.getState())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userReq.getEmail())
                .phoneNumber(userReq.getPhoneNumber())
                .accountBalance(BigDecimal.ZERO)
                .alternateNumber(userReq.getAlternateNumber())
                .status("ACTIVE")
                .password(passwordEncoder.encode(userReq.getPassword()))
                .build();

        userRepo.save(newUser);

        ConfirmationToken confirmationToken = new ConfirmationToken(newUser);

        confirmationTokenRepo.save(confirmationToken);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userReq.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/confirm/account?token=" + confirmationToken.getConfirmationToken());

        emailservice.mailSend(mailMessage);

        return "check your email to verify";
    }
    public BankResponse activateAccount(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepo.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepo.findByEmail(token.getUser().getEmail())
                    .orElseThrow();
            user.setEnabled(true);
            userRepo.save(user);
            EmailDeets emailDeets = EmailDeets.builder()
                    .recipient(user.getEmail())
                    .subject("Account Creation")
                    .messageBody("Congrats! Your  account has been created successfully. \n" +
                            "Account Name: " + user.getFirstName() + " " + user.getLastName() + "\n Account Number:" +
                            user.getAccountNumber())
                    .build();
            emailservice.sendEmailAlert(emailDeets);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(user.getAccountBalance())
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName())
                            .build())
                    .build();
        }
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                .accountInfo(null)
                .build();
    }
}