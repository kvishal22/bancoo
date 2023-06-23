package com.kanna.banco.service;

import com.kanna.banco.dto.*;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.statement.TransactionDto;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo repo;
    private final Emailservice emailservice;
    private final TransactionService transactionService;

   @Override
    public BankResponse balanceEnquiry(EnquiryReq enquiryReq) {
        Boolean isAccountExist = repo.existsByAccountNumber(enquiryReq.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = repo.findByAccountNumber(enquiryReq.getAccountNumber());
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
    @Override
    public String nameEnquiry(EnquiryReq enquiryReq) {
        Boolean isAccountExist = repo.existsByAccountNumber(enquiryReq.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = repo.findByAccountNumber(enquiryReq.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitReq creditDebitReq) {
        Boolean isAccountExist = repo.existsByAccountNumber(creditDebitReq.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = repo.findByAccountNumber(creditDebitReq.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitReq.getAmount()));
        repo.save(userToCredit);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("credit")
                .amount(userToCredit.getAccountBalance())
                .balanceAfterTransaction(userToCredit.getAccountBalance().add(creditDebitReq.getAmount()))
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


    @Override
    public TransferResponse transferMoney(TransferMoney req) {
        Boolean isFromAccountExist = repo.existsByAccountNumber(req.getFromAccountNumber());
        Boolean isToAccountExist = repo.existsByAccountNumber(req.getToAccountNumber());
        if (!isFromAccountExist || !isToAccountExist) {
            return TransferResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User fromAccountNumber = repo.findByAccountNumber(req.getFromAccountNumber());
        User toAccountNumber = repo.findByAccountNumber(req.getToAccountNumber());

        BigInteger availBal = fromAccountNumber.getAccountBalance().toBigInteger();
        BigInteger amountToCredit = req.getAmount().toBigInteger();
        if (availBal.intValue() < amountToCredit.intValue()) {
            return TransferResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else{
            fromAccountNumber.setAccountBalance(fromAccountNumber.getAccountBalance().subtract(req.getAmount()));
            toAccountNumber.setAccountBalance(toAccountNumber.getAccountBalance().add(req.getAmount()));
            repo.save(fromAccountNumber);
            repo.save(toAccountNumber);
            EmailDeets emailDetails = EmailDeets.builder()
                    .recipient(fromAccountNumber.getEmail())
                    .subject("ACCOUNT TRANSFER")
                    .messageBody("amount of " +req.getAmount()+ " has been debited" )
                    .build();
            emailservice.sendEmailAlert(emailDetails);

            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(toAccountNumber.getAccountNumber())
                    .transactionType("credit")
                    .amount(fromAccountNumber.getAccountBalance())
                    .balanceAfterTransaction(toAccountNumber.getAccountBalance())
                    .build();
            transactionService.saveTransaction(transactionDto);

            TransactionDto transactionDtoTwo = TransactionDto.builder()
                    .accountNumber(fromAccountNumber.getAccountNumber())
                    .transactionType("debit")
                    .amount(toAccountNumber.getAccountBalance())
                    .balanceAfterTransaction(fromAccountNumber.getAccountBalance())
                    .build();
            transactionService.saveTransaction(transactionDtoTwo);

            return TransferResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .debitedAmont(req.getAmount())
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(req.getFromAccountNumber())
                            .accountName(fromAccountNumber.getFirstName()+" "+fromAccountNumber.getLastName())
                            .accountBalance(fromAccountNumber.getAccountBalance())
                            .build())
                    .build();
        }

    }
}
