package com.kanna.banco.utils;
import java.time.Year;
import java.util.Random;

public class AccountUtils {
    public static final String OTP_EXPIRED = "otp expired please create a new one" ;
    public static final String OTP_GENERATED = "the link has been successfully sent to your email address, it will expire within 3 minutes";
    public static final String AMOUNT_DEBITED_ALREADY = "your money has been debited already";
    public static final String AMOUNT_PAID_ALREADY = "501";

    private AccountUtils(){

    }
    public static final String INVALID_TOKEN_CODE = "400";
    public static final String INVALID_TOKEN_MESSAGE = "token is not valid";
    public static final String ACCOUNT_ACTIVATION_SUCCESS = "200" ;
    public static final String ACCOUNT_ACTIVATION_MESSAGE = "Your account created successfully, please log in";
    public static final String UNAUTHORIZED = "401";
    public static final String INTERNAL_SERVER_ERROR = "500";
    public static final String BAD_REQUEST = "400";
    public static final String ACCOUNT_HOLDER_NAME = "Account Holder Name: ";
    public static final String CHECK_EMAIL = "check your email to change the password";
    public static final String ACCOUNT_EXISTS_CODE = "200";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user has an account already, please log in";
    public static final String ACCOUNT_CREATION_SUCCESS = "201";
    public static final String ACCOUNT_CREATION_MESSAGE = "Your account has been registered successfully, check your email to activate";
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
    public static final String INVALID_DETAILS = "Invalid username or password, please enter correct details";
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
    public static String generateOtp(){
        int min = 1000;
        int max=9999;
        Random random = new Random();
        int randNum = random.nextInt(max-min+1)+min;
        StringBuilder sb = new StringBuilder();
        return sb.append(randNum).toString();

    }
    }

