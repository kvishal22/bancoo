package com.kanna.banco.password;

import lombok.Data;

@Data
public class PasswordForgotReq {
    private String accountNumber;
    private String email;
}