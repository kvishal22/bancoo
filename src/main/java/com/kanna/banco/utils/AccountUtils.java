package com.kanna.banco.utils;
import java.time.Year;
import java.util.Random;

public class AccountUtils {

    private AccountUtils(){

    }
    public static final String ACCOUNT_EXISTS_CODE = "200";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user has an account already, please log in";
    public static final String ACCOUNT_CREATION_SUCCESS = "201";
    public static final String ACCOUNT_CREATION_MESSAGE = "Your account has been created successfully";
    public static final String ACCOUNT_NOT_EXIST_CODE = "500";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "account number does not exist or your password/username is incorrect";
    public static final String ACCOUNT_FOUND_CODE = "202";
    public static final String ACCOUNT_FOUND_MESSAGE = "User account found";
    public static final String ACCOUNT_CREDITED_SUCCESS ="203";
    public static final String  ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Money credited Successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "501";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "You don't have enough balance to transfer money / You cannot transfer zero ";
    public static final String ACCOUNT_DEBITED_SUCCESS = "204" ;
    public static final String ACCOUNT_DEBITED_MESSAGE = "Your money has been debited";
    public static final String YOU_CANT_TRANSEFER_ZERO = "you cannot transfer zero as an amount";
    public static final String INVALID_DETAILS = "enter valid details";
    public static final String PASSWORD_CHANGED = "password changed successfully";

    public static String generateAccountNumber(){

        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        Random random = new Random();

        int randNumber = random.nextInt(max - min + 1) + min;

        String year = String.valueOf(currentYear);

        String randomNumber = String.valueOf(randNumber);

        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }
    }

