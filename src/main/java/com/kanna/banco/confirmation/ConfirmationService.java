package com.kanna.banco.confirmation;

import com.kanna.banco.dto.AccountInfo;
import com.kanna.banco.dto.BankResponse;
import com.kanna.banco.dto.EmailDeets;
import com.kanna.banco.dto.UserReq;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.dto.PasswordForgotEntity;
import com.kanna.banco.dto.PasswordForgotReq;
import com.kanna.banco.service.Emailservice;
import com.kanna.banco.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ConfirmationService {

    private final UserRepo userRepo;
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final Emailservice emailservice;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public BankResponse registerUser(UserReq userReq) throws MessagingException {

        if (userRepo.existsByEmail(userReq.getEmail()) || userRepo.existsByPhoneNumber(userReq.getPhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                    .build();
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
                //.role(userReq.getRole())
                .build();

       userRepo.save(newUser);

        ConfirmationTokenDetail confirmationTokenDetail = new ConfirmationTokenDetail(newUser);
        confirmationTokenRepo.save(confirmationTokenDetail);

        String confirmationLink = "http://localhost:8080/confirm/account?token=" + confirmationTokenDetail.getConfirmationToken();
        emailservice.sendVerificationEmail(newUser.getEmail(),confirmationLink);

        logger.info("email confirmation sent to: {}", newUser.getEmail());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .build();
    }
    public BankResponse activateAccount(String confirmationToken) {
        ConfirmationTokenDetail token = confirmationTokenRepo.findByConfirmationToken(confirmationToken);
        if (token == null) {
            logger.error("Invalid token: {}", confirmationToken);
            return BankResponse.builder()
                    .responseCode(AccountUtils.INVALID_TOKEN_CODE)
                    .responseMessage(AccountUtils.INVALID_TOKEN_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User user = token.getUser();
        if (user.isActive()) {
            //changed to isActive since isEnable is already there from userdetails interface
            logger.info("Account already activated for user: {}", user.getEmail());
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        logger.info("Enabled account for user: {}", user.getEmail());
        user.setActive(true);
        userRepo.save(user);

        EmailDeets emailDeets = EmailDeets.builder()
                .recipient(user.getEmail())
                .subject("Account Activation")
                .messageBody("Congratulations! Your account has been activated successfully.")
                .build();
        emailservice.sendEmailAlert(emailDeets);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_ACTIVATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_ACTIVATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(user.getAccountBalance())
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .build())
                .build();
    }

    public BankResponse forgotPassword(PasswordForgotEntity passwordForgotEntity) {
        Boolean isAccountExist = userRepo.existsByEmail(passwordForgotEntity.getEmail());
        Boolean isExist = confirmationTokenRepo
                .existsByConfirmationToken(passwordForgotEntity.getConfirmationToken());
        User user = userRepo.findByAccountNumber(passwordForgotEntity.getAccountNumber());

        if (Boolean.TRUE.equals(isAccountExist) && Boolean.TRUE.equals(isExist)) {
            String encodedPassword = passwordEncoder.encode(passwordForgotEntity.getNewPassword());
                user.setPassword(encodedPassword);
                userRepo.save(user);
                logger.info("password changed by: {}",user.getEmail());
                return BankResponse.builder()
                        .responseMessage(AccountUtils.PASSWORD_CHANGED).build();

        } else {
            logger.info("invalid details by: {}",user.getEmail());
            return BankResponse.builder()
                    .responseMessage(AccountUtils.INVALID_DETAILS)
                    .build();
        }
    }
    public BankResponse requestPasswordChange(PasswordForgotReq passwordForgotReq) {
        Boolean isAccountExist = userRepo.existsByEmail(passwordForgotReq.getEmail());
        User user = userRepo.findByAccountNumber(passwordForgotReq.getAccountNumber());

        if (user!=null && Boolean.TRUE.equals(isAccountExist)) {

            ConfirmationTokenDetail confirmationTokenDetail = generateConfirmationToken(user);
            confirmationTokenDetail.setCreatedAt(new Date());
            confirmationTokenRepo.save(confirmationTokenDetail);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(passwordForgotReq.getEmail());
            mailMessage.setSubject("Request New Password!");
            mailMessage.setText("to change this password enter this token : "
                    + confirmationTokenDetail.getConfirmationToken());
            emailservice.mailSend(mailMessage);
            logger.info("password request sent to: {}",user.getEmail());

            return BankResponse.builder()
                    .responseMessage(AccountUtils.CHECK_EMAIL)
                    .build();
        }
        else {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.INVALID_DETAILS)
                    .build();
        }
    }
    private ConfirmationTokenDetail generateConfirmationToken(User user) {

        ConfirmationTokenDetail confirmationTokenDetail = new ConfirmationTokenDetail();
        confirmationTokenDetail.setUser(user);

        String token = UUID.randomUUID().toString();

        confirmationTokenDetail.setConfirmationToken(token);

        confirmationTokenRepo.save(confirmationTokenDetail);

        return confirmationTokenDetail;
    }

}