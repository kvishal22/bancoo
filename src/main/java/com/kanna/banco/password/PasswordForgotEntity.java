package com.kanna.banco.password;

import lombok.Data;

@Data
public class PasswordForgotEntity {
    private String email;
    private String newPassword;
    private String confirmationToken;
    private String accountNumber;
}
