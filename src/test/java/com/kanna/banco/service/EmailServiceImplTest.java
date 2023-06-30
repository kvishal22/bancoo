package com.kanna.banco.service;

import com.kanna.banco.dto.EmailDeets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
 class EmailServiceImplTest {

   @Mock
    private JavaMailSender javaMailSender;
    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(javaMailSender);
    }

    @Test
     void sendEmailAlert() {
        String recipientEmail = "recipient@example.com";
        String subject = "Test Subject";
        String messageBody = "Test Message Body";
        String attachment = "vk";

        EmailDeets emailDeets = new EmailDeets(recipientEmail,messageBody,subject,attachment);
        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setTo(recipientEmail);
        expectedMailMessage.setSubject(subject);
        expectedMailMessage.setText(attachment);
        expectedMailMessage.setText(messageBody);

        doNothing().when(javaMailSender).send(expectedMailMessage);

        emailService.sendEmailAlert(emailDeets);

        verify(javaMailSender, times(1)).send(expectedMailMessage);
        verifyNoMoreInteractions(javaMailSender);
    }
   @Test
     void testSendEmailAlertThrownRuntimeExceptionThrown() {
       EmailDeets emailDeets = new EmailDeets();
       emailDeets.setRecipient("recipient@example.com");
       emailDeets.setSubject("Test Email");
       emailDeets.setMessageBody("This is a test email.");


       doThrow(new MailSendException("exception")).when(javaMailSender).send(any(SimpleMailMessage.class));

       RuntimeException exception = assertThrows(RuntimeException.class, () -> emailService.sendEmailAlert(emailDeets));
       Assertions.assertEquals("exception", exception.getMessage());

    }
    @Test
     void testMailSend(){

        SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
        expectedMailMessage.setTo("recipient@example.com");
        expectedMailMessage.setSubject("subject");
        expectedMailMessage.setText("attachment");
        expectedMailMessage.setText("messageBody");

        emailService.mailSend(expectedMailMessage);

        verify(javaMailSender).send(expectedMailMessage);
        verifyNoMoreInteractions(javaMailSender);

    }
}

