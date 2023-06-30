package com.kanna.banco.service;

import com.kanna.banco.dto.EmailDeets;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements Emailservice {

    private final JavaMailSender javaMailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

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
            logger.info("Mail sent successfully to {}", emailDeets.getRecipient());
        } catch (MailSendException exception) {
            throw new MailSendException("exception");
        }
    }

    @Async
    public void mailSend(SimpleMailMessage mailMessage) {
        javaMailSender.send(mailMessage);
    }

}
