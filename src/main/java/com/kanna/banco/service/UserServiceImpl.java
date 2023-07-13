package com.kanna.banco.service;

import com.kanna.banco.dto.*;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.dto.PasswordChangeEntity;
import com.kanna.banco.statement.TransactionDto;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo repo;
    private final Emailservice emailservice;
    private final TransactionService transactionService;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public BankResponse balanceEnquiry(EnquiryReq enquiryReq) {
        Boolean isAccountExist = repo.existsByAccountNumber(enquiryReq.getAccountNumber());
        User foundUser = repo.findByAccountNumber(enquiryReq.getAccountNumber());
        if (Boolean.TRUE.equals(isAccountExist) &&
                passwordEncoder.matches(enquiryReq.getPassword(), foundUser.getPassword())) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountBalance(foundUser.getAccountBalance())
                                .accountNumber(enquiryReq.getAccountNumber())
                                .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                                .build())
                        .build();
        }
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                .accountInfo(null)
                .build();
    }
    @Override
    public BankResponse nameEnquiry(EnquiryReq enquiryReq) {

        Boolean isAccountExist = repo.existsByAccountNumber(enquiryReq.getAccountNumber());
        User foundUser = repo.findByAccountNumber(enquiryReq.getAccountNumber());

        if (Boolean.TRUE.equals(isAccountExist) &&
                passwordEncoder.matches(enquiryReq.getPassword(), foundUser.getPassword())) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_HOLDER_NAME +foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build();

        }
        return BankResponse.builder()
                .responseMessage(AccountUtils.INVALID_DETAILS)
                .build();
    }

    @Override
    @Transactional
    public BankResponse creditAccount(CreditDebitReq creditDebitReq) {
        Boolean isAccountExist = repo.existsByAccountNumber(creditDebitReq.getAccountNumber());
        User userToCredit = repo.findByAccountNumber(creditDebitReq.getAccountNumber());
        if (Boolean.TRUE.equals(isAccountExist) &&
                passwordEncoder.matches(creditDebitReq.getPassword(), userToCredit.getPassword())) {
            if (creditDebitReq.getAmount().equals(BigDecimal.valueOf(0.0)) ||
                    creditDebitReq.getAmount().equals(BigDecimal.valueOf(0))) {
                return BankResponse.builder()
                        .responseMessage(AccountUtils.YOU_CANT_TRANSEFER_ZERO)
                        .build();
            } else {
                userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitReq.getAmount()));
                repo.save(userToCredit);
                TransactionDto transactionDto = TransactionDto.builder()
                        .accountNumber(userToCredit.getAccountNumber())
                        .transactionType("credit")
                        .amount(creditDebitReq.getAmount())
                        .balanceAfterTransaction(userToCredit.getAccountBalance())
                        .build();
                transactionService.saveTransaction(transactionDto);
                logger.info("money has been credidted to ac no: {}",userToCredit.getEmail());
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                        .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                                .accountBalance(userToCredit.getAccountBalance())
                                .accountNumber(creditDebitReq.getAccountNumber())
                                .build())
                        .build();
            }
        } else {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.INVALID_DETAILS)
                    .build();
        }
    }

    @Override
    @Transactional
    public TransferResponse transferMoney(TransferMoney req) {
        User fromAccountNumber = repo.findByAccountNumber(req.getFromAccountNumber());
        User toAccountNumber = repo.findByAccountNumber(req.getToAccountNumber());

        if (fromAccountNumber == null || toAccountNumber == null ||
                !passwordEncoder.matches(req.getPassword(), fromAccountNumber.getPassword())) {
            return TransferResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        BigDecimal availBal = fromAccountNumber.getAccountBalance();
        BigDecimal amountToCredit = req.getAmount();

        if (availBal.compareTo(amountToCredit) < 0 || amountToCredit.compareTo(BigDecimal.ZERO) <= 0) {
            return TransferResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        fromAccountNumber.setAccountBalance(availBal.subtract(amountToCredit));
        toAccountNumber.setAccountBalance(toAccountNumber.getAccountBalance().add(amountToCredit));
        repo.save(fromAccountNumber);
        repo.save(toAccountNumber);

        logger.info("money has been debited from: {}",fromAccountNumber.getEmail());

        EmailDeets emailDetails = EmailDeets.builder()
                .recipient(fromAccountNumber.getEmail())
                .subject("IMPS Transaction - Success")
                .messageBody("We wish to inform you that an amount of " + req.getAmount() + " has been debited from your account.")
                .build();
        emailservice.sendEmailAlert(emailDetails);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(toAccountNumber.getAccountNumber())
                .transactionType("credit")
                .amount(req.getAmount())
                .balanceAfterTransaction(toAccountNumber.getAccountBalance())
                .build();
        transactionService.saveTransaction(transactionDto);

        TransactionDto transactionDtoTwo = TransactionDto.builder()
                .accountNumber(fromAccountNumber.getAccountNumber())
                .transactionType("debit")
                .amount(req.getAmount())
                .balanceAfterTransaction(fromAccountNumber.getAccountBalance())
                .build();
        transactionService.saveTransaction(transactionDtoTwo);

        return TransferResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                .debitedAmount(req.getAmount())
                .accountInfo(AccountInfo.builder()
                        .accountNumber(req.getFromAccountNumber())
                        .accountName(fromAccountNumber.getFirstName() + " " + fromAccountNumber.getLastName())
                        .accountBalance(fromAccountNumber.getAccountBalance())
                        .build())
                .build();
    }

    public BankResponse changePassword(PasswordChangeEntity passwordChangeEntity){
            Boolean isAccountExist = repo.existsByEmail(passwordChangeEntity.getEmail());
            if(Boolean.FALSE.equals(isAccountExist)){
                return BankResponse.builder()
                        .responseMessage(AccountUtils.INVALID_DETAILS)
                        .build();
            }
            User user = repo.findByAccountNumber(passwordChangeEntity.getAccountNumber());
            if(Boolean.FALSE.equals(passwordEncoder
                    .matches(passwordChangeEntity.getCurrentPassword(), user.getPassword()))){

                return BankResponse.builder()
                        .responseMessage(AccountUtils.INVALID_DETAILS)
                        .build();
            }
            user.setPassword(passwordEncoder.encode(passwordChangeEntity.getNewPassword()));
            repo.save(user);
            return BankResponse.builder()
                    .responseMessage(AccountUtils.PASSWORD_CHANGED)
                    .build();
    }

}
