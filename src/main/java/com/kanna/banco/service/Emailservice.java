package com.kanna.banco.service;

import com.kanna.banco.dto.EmailDeets;
import org.springframework.mail.SimpleMailMessage;


public interface Emailservice {
    void sendEmailAlert(EmailDeets emailDeets);
    void mailSend(SimpleMailMessage mailMessage);
}
