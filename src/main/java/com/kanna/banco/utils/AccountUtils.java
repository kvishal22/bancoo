package com.kanna.banco.utils;

import java.math.BigDecimal;
import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "200";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user has an account already, please log in";
    public static final String ACCOUNT_CREATION_SUCCESS = "201";
    public static final String ACCOUNT_CREATION_MESSAGE = "Your account has been created successfully";
    public static final String ACCOUNT_NOT_EXIST_CODE = "500";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "The account number does not exist";
    public static final String ACCOUNT_FOUND_CODE = "202";
    public static final String ACCOUNT_FOUND_MESSAGE = "User account found";
    public static final String ACCOUNT_CREDITED_SUCCESS ="203";
    public static final String  ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Money credited Successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "501";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "You don't have enough balance to transfer money";
    public static final String ACCOUNT_DEBITED_SUCCESS = "204" ;
    public static final String ACCOUNT_DEBITED_MESSAGE = "Your money has been debited";
    public static final BigDecimal NOT_FOUND = null;

    public static String generateAccountNumber() {

        Year currentYear = Year.now();

        int min = 100000;
        int max = 999999;

        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        String year = String.valueOf(currentYear);

        String randomNumber = String.valueOf(randNumber);

        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }

}
