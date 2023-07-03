package com.kanna.banco.dto;

import lombok.Data;

@Data
public class PasswordChangeEntity {
    private String email;
    private String newPassword;
    private String currentPassword;
    private String accountNumber;
}
