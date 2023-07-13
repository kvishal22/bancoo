package com.kanna.banco.exception;

import com.kanna.banco.dto.BankResponse;
import com.kanna.banco.utils.AccountUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BankResponse handleValidationException(MethodArgumentNotValidException e){
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        Map<String,String> map= new HashMap<>();
        errors.forEach( error -> {
            String key = ((FieldError) error).getField();
            String val = error.getDefaultMessage();
            map.put(key, val);
        });
        return BankResponse.builder()
                .responseCode(AccountUtils.BAD_REQUEST)
                .responseMessage("Invalid arguments")
                .build();
    }
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BankResponse handleJwtException(ExpiredJwtException e) {

        return BankResponse.builder()
                .responseMessage("Invalid or expired JWT token")
                .responseCode(AccountUtils.UNAUTHORIZED)
                .build();
    }
    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public BankResponse handleJwtExceptionTwo(MalformedJwtException e) {
        return BankResponse.builder()
                .responseCode(AccountUtils.UNAUTHORIZED)
                .responseMessage("Authentication credentials not as expected")
                .build();
    }
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BankResponse handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException e){
        return BankResponse.builder()
                .responseMessage("Authentication credentials not found")
                .responseCode(AccountUtils.UNAUTHORIZED)
                .build();
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BankResponse handleBadCredentialsException(BadCredentialsException e){
        return BankResponse.builder()
                .responseCode(AccountUtils.UNAUTHORIZED)
                .responseMessage(AccountUtils.INVALID_DETAILS)
                .build();
    }


   @ExceptionHandler(SignatureException.class)
   @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BankResponse handleSignatureException(SignatureException e) {
        return BankResponse.builder()
                .responseCode(AccountUtils.UNAUTHORIZED)
                .responseMessage("invalid jwt signature")
                .build();
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BankResponse handleGenericException(Exception e) {
        return BankResponse.builder()
                .responseCode(AccountUtils.INTERNAL_SERVER_ERROR)
                .responseMessage("A server error occurs")
                .build();
    }

}
