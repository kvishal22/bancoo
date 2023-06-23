package com.kanna.banco.service;

import com.kanna.banco.dto.EmailDeets;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;


public interface Emailservice {
    void sendEmailAlert(EmailDeets emailDeets);
    public void mailSend(SimpleMailMessage mailMessage);
}
