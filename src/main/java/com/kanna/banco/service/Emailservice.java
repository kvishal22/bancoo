package com.kanna.banco.service;

import com.kanna.banco.dto.EmailDeets;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;


public interface Emailservice {
    void sendEmailAlert(EmailDeets emailDeets);
    void mailSend(SimpleMailMessage mailMessage);
    void sendVerificationEmail(String recipientEmail, String confirmationLink) throws MessagingException;
    void sendOtpEmail(String recipientEmail, String otp) throws MessagingException;
}
