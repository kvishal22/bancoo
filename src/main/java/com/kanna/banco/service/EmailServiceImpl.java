package com.kanna.banco.service;

import com.kanna.banco.dto.EmailDeets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements Emailservice {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;


    @Override
    public void sendEmailAlert(EmailDeets emailDeets) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDeets.getRecipient());
            simpleMailMessage.setText(emailDeets.getMessageBody());
            simpleMailMessage.setSubject(emailDeets.getSubject());

            javaMailSender.send(simpleMailMessage);
            System.out.println("Mail sent successfully to " + emailDeets.getRecipient());

        } catch (MailException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Async
    public void mailSend(SimpleMailMessage mailMessage) {
        javaMailSender.send(mailMessage);
    }
}
