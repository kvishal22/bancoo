package com.kanna.banco.exception;

import com.kanna.banco.dto.BankResponse;
import com.kanna.banco.utils.AccountUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;


import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionHandlerTest {

    private final ExceptionHandlerAdvice exceptionHandler = new ExceptionHandlerAdvice();

    @Test
     void testExpiredJwtExceptionShouldReturnUnauthorized() {
        ExpiredJwtException exception = new ExpiredJwtException(null, null,"Token expired");

        BankResponse response = exceptionHandler.handleJwtException(exception);

        assertEquals(AccountUtils.UNAUTHORIZED, response.getResponseCode());
        assertEquals("Invalid or expired JWT token", response.getResponseMessage());
    }

    @Test
     void testMalformedJwtExceptionShouldReturnUnauthorized() {

        MalformedJwtException exception = new MalformedJwtException("Invalid token");

        BankResponse response = exceptionHandler.handleJwtExceptionTwo(exception);

        assertEquals(AccountUtils.UNAUTHORIZED, response.getResponseCode());
        assertEquals("Authentication credentials not as expected", response.getResponseMessage());
    }
    @Test
    void testAuthenticationCredentialsNotFoundException(){

        AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException("Credentials not found");

        BankResponse response = exceptionHandler.handleAuthenticationCredentialsNotFoundException(exception);

        assertEquals(AccountUtils.UNAUTHORIZED, response.getResponseCode());
        assertEquals("Authentication credentials not found",response.getResponseMessage());
    }

    @Test
     void testBadCredentialsExceptionShouldReturnUnauthorized() {

        BadCredentialsException exception = new BadCredentialsException("Invalid details");

        BankResponse response = exceptionHandler.handleBadCredentialsException(exception);

        assertEquals(AccountUtils.UNAUTHORIZED, response.getResponseCode());
        assertEquals(AccountUtils.INVALID_DETAILS, response.getResponseMessage());
    }


    @Test
     void testSignatureExceptionShouldReturnUnauthorized() {
        SignatureException exception = new SignatureException("Invalid token");

        BankResponse response = exceptionHandler.handleSignatureException(exception);

        assertEquals(AccountUtils.UNAUTHORIZED, response.getResponseCode());
        assertEquals("invalid jwt signature", response.getResponseMessage());
    }

    @Test
     void testGenericExceptionShouldReturnInternalServerError() {
        Exception exception = new Exception("Some error");

        BankResponse response = exceptionHandler.handleGenericException(exception);

        assertEquals(AccountUtils.INTERNAL_SERVER_ERROR, response.getResponseCode());
        assertEquals("A server error occurs", response.getResponseMessage());
    }
}

