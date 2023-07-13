package com.kanna.banco.service;

import com.kanna.banco.dto.EmailDeets;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


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
    public void sendVerificationEmail(String recipientEmail, String confirmationLink) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(recipientEmail);
        mimeMessageHelper.setSubject("Verify your account");
        String emailBody = "<div>\n" +
                "    <a href=\"" + confirmationLink + "\" target=\"_blank\">Click this link to verify your account</a>\n" +
                "</div>";

        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(emailBody, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        mimeMessage.setContent(multipart);
        javaMailSender.send(mimeMessage);
    }
    @Override
    public void sendOtpEmail(String otp,String recipientEmail) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(recipientEmail);
        mimeMessageHelper.setSubject("You shall use this Otp link to pay");
        mimeMessageHelper.setText("http://localhost:8080/merchant/payToMerchant?otp="+otp+"&email="+recipientEmail);
        javaMailSender.send(mimeMessage);
    }

}
