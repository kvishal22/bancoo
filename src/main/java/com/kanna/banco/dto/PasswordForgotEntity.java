package com.kanna.banco.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PasswordForgotEntity {
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 8, message = "password should have at least 8 characters")
    private String newPassword;

    @NotEmpty
    private String confirmationToken;
    @NotEmpty
    private String accountNumber;
}
