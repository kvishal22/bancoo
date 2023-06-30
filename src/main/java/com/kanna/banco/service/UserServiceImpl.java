package com.kanna.banco.service;

import com.kanna.banco.dto.*;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.password.PasswordChangeEntity;
import com.kanna.banco.statement.TransactionDto;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo repo;
    private final Emailservice emailservice;
    private final TransactionService transactionService;
    private final PasswordEncoder passwordEncoder;

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
    public String nameEnquiry(EnquiryReq enquiryReq) {

        Boolean isAccountExist = repo.existsByAccountNumber(enquiryReq.getAccountNumber());
        User foundUser = repo.findByAccountNumber(enquiryReq.getAccountNumber());

        if (Boolean.TRUE.equals(isAccountExist) &&
                passwordEncoder.matches(enquiryReq.getPassword(), foundUser.getPassword())) {
                return foundUser.getFirstName() + " " + foundUser.getLastName();
        }
        return AccountUtils.INVALID_DETAILS;
    }

    @Override
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
                    .responseCode(AccountUtils.INVALID_DETAILS)
                    .build();
        }
    }

    @Override
    public TransferResponse transferMoney(TransferMoney req) {
        Boolean isFromAccountExist = repo.existsByAccountNumber(req.getFromAccountNumber());
        Boolean isToAccountExist = repo.existsByAccountNumber(req.getToAccountNumber());
        User fromAccountNumber = repo.findByAccountNumber(req.getFromAccountNumber());
        User toAccountNumber = repo.findByAccountNumber(req.getToAccountNumber());

        if (Boolean.TRUE.equals(isFromAccountExist) && Boolean.TRUE.equals(isToAccountExist) &&
                passwordEncoder.matches(req.getPassword(),fromAccountNumber.getPassword())) {

            BigInteger availBal = fromAccountNumber.getAccountBalance().toBigInteger();
            BigInteger amountToCredit = req.getAmount().toBigInteger();

            if (availBal.intValue() < amountToCredit.intValue() || amountToCredit.equals(BigInteger.valueOf(0)))
                return TransferResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();

            fromAccountNumber.setAccountBalance(fromAccountNumber.getAccountBalance().subtract(req.getAmount()));
            toAccountNumber.setAccountBalance(toAccountNumber.getAccountBalance().add(req.getAmount()));
            repo.save(fromAccountNumber);
            repo.save(toAccountNumber);

            EmailDeets emailDetails = EmailDeets.builder()
                    .recipient(fromAccountNumber.getEmail())
                    .subject("ACCOUNT TRANSFER")
                    .messageBody("amount of " + req.getAmount() + " has been debited")
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

        } else {
            return TransferResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
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
