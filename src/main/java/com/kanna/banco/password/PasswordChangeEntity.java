package com.kanna.banco.password;

import lombok.Data;

@Data
public class PasswordChangeEntity {
    private String email;
    private String newPassword;
    private String currentPassword;
    private String accountNumber;
}
