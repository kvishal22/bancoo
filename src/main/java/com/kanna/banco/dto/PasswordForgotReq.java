package com.kanna.banco.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PasswordForgotReq {
    @NotEmpty
    private String accountNumber;
    @NotEmpty
    private String email;
}
