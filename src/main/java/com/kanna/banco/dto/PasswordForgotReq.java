package com.kanna.banco.dto;

import lombok.Data;

@Data
public class PasswordForgotReq {
    private String accountNumber;
    private String email;
}
