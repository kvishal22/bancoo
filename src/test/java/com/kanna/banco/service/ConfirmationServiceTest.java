package com.kanna.banco.service;

import com.kanna.banco.confirmation.ConfirmationService;
import com.kanna.banco.confirmation.ConfirmationTokenDetail;
import com.kanna.banco.confirmation.ConfirmationTokenRepo;
import com.kanna.banco.dto.*;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.utils.AccountUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConfirmationServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private ConfirmationTokenRepo confirmationTokenRepo;
    @Mock
    private Emailservice emailservice;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private ConfirmationService confirmationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        confirmationService = new ConfirmationService(
                userRepo,
                confirmationTokenRepo,
                emailservice,
                passwordEncoder
        );
    }

    @Test
     void testRegisterUserWhenUserDoesNotExist() throws MessagingException {
        UserReq userReq = new UserReq();
        userReq.setFirstName("vishal");
        userReq.setLastName("kanna");
        userReq.setEmail("vishalk@gmail.com");
        userReq.setPhoneNumber("972738443");

        when(userRepo.existsByEmail(userReq.getEmail())).thenReturn(false);
        when(userRepo.existsByPhoneNumber(userReq.getPhoneNumber())).thenReturn(false);

        when(userRepo.save(any(User.class))).thenReturn(new User());

        when(confirmationTokenRepo.save(any(ConfirmationTokenDetail.class))).thenReturn(new ConfirmationTokenDetail());

        BankResponse result = confirmationService.registerUser(userReq);

        verify(userRepo, times(0)).findByEmail(userReq.getEmail());
        verify(userRepo, times(1)).existsByPhoneNumber(userReq.getPhoneNumber());
        verify(userRepo, times(1)).existsByEmail(userReq.getEmail());

        verify(userRepo, times(1)).save(any(User.class));

        verify(confirmationTokenRepo, times(1)).save(any(ConfirmationTokenDetail.class));
        verify(emailservice).mailSend(any(SimpleMailMessage.class));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(AccountUtils.ACCOUNT_CREATION_MESSAGE, result.getResponseMessage());
    }


    @Test
     void testUserAlreadyExistsByPhoneNumber() throws MessagingException{
        UserReq userReq = new UserReq();
        userReq.setFirstName("vishal");
        userReq.setLastName("kanna");
        userReq.setEmail("vishalk@gmail.com");
        userReq.setPhoneNumber("9878987899");

        when(userRepo.existsByEmail(userReq.getEmail())).thenReturn(false);
        when(userRepo.existsByPhoneNumber(userReq.getPhoneNumber())).thenReturn(true);

        BankResponse result = confirmationService.registerUser(userReq);

        verify(userRepo, times(0)).findByEmail(userReq.getEmail());
        verify(userRepo, times(1)).existsByPhoneNumber(userReq.getPhoneNumber());
        verify(userRepo, times(1)).existsByEmail(userReq.getEmail());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(AccountUtils.ACCOUNT_FOUND_MESSAGE, result.getResponseMessage());
    }
    @Test
     void testUserAlreadyExistsByEmail() throws MessagingException{
        UserReq userReq = new UserReq();
        userReq.setFirstName("vishal");
        userReq.setLastName("kanna");
        userReq.setEmail("vishalk@gmail.com");
        userReq.setPhoneNumber("9878987899");

        when(userRepo.existsByEmail(userReq.getEmail())).thenReturn(true);
        when(userRepo.existsByPhoneNumber(userReq.getPhoneNumber())).thenReturn(false);

        BankResponse result = confirmationService.registerUser(userReq);

        verify(userRepo, times(0)).findByEmail(userReq.getEmail());
        verify(userRepo, times(0)).existsByPhoneNumber(userReq.getPhoneNumber());
        verify(userRepo, times(1)).existsByEmail(userReq.getEmail());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(AccountUtils.ACCOUNT_FOUND_MESSAGE, result.getResponseMessage());
    }
    @Test
    void testActivateAccountFails(){
        String confirmationToken = "invalid_token";

        Mockito.when(confirmationTokenRepo.findByConfirmationToken(confirmationToken))
                .thenReturn(null);

        BankResponse response = confirmationService.activateAccount(confirmationToken);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(AccountUtils.INVALID_TOKEN_CODE, response.getResponseCode());
        Assertions.assertEquals(AccountUtils.INVALID_TOKEN_MESSAGE, response.getResponseMessage());

        verify(userRepo, Mockito.never()).save(any(User.class));
        verify(emailservice, Mockito.never()).sendEmailAlert(any(EmailDeets.class));

    }
    @Test
    void testActivateAccountNotFails(){
        User user = new User();
        user.setEmail("dadsd");
        user.setAccountNumber("323");
        user.setAccountBalance(BigDecimal.valueOf(0));
        user.setFirstName("visha");
        user.setLastName("k");
        user.setEmail("vk");

        ConfirmationTokenDetail confirmationTokenDetail1 = new ConfirmationTokenDetail();

        String confirmationToken = "DKJSNKJSN";

        confirmationTokenDetail1.setUser(user);

        Mockito.when(confirmationTokenRepo.findByConfirmationToken(confirmationToken))
                .thenReturn(confirmationTokenDetail1);
        Mockito.when(userRepo.findByEmail(confirmationTokenDetail1.getUser().getEmail()))
               .thenReturn(Optional.of(user));

        BankResponse response = confirmationService.activateAccount(confirmationToken);
        verify(userRepo).save(user);
        verify(emailservice).sendEmailAlert(any(EmailDeets.class));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AccountUtils.ACCOUNT_ACTIVATION_SUCCESS, response.getResponseCode());
        Assertions.assertEquals(AccountUtils.ACCOUNT_ACTIVATION_MESSAGE, response.getResponseMessage());
        Assertions.assertEquals(user.getAccountBalance(), response.getAccountInfo().getAccountBalance());
        Assertions.assertEquals(user.getAccountNumber(), response.getAccountInfo().getAccountNumber());
        Assertions.assertEquals(user.getFirstName() + " " + user.getLastName(), response.getAccountInfo().getAccountName());

    }

    @Test
    void testActivateAccountFailsIfAlreadyHasANAccount(){
        User user = new User();
        user.setEmail("dadsd");
        user.setActive(true);    //if its false new account will be created
        user.setAccountNumber("323");
        user.setAccountBalance(BigDecimal.valueOf(0));
        user.setFirstName("visha");
        user.setLastName("k");
        user.setEmail("vk");

        ConfirmationTokenDetail confirmationTokenDetail1 = new ConfirmationTokenDetail();

        String confirmationToken = "DKJSNKJSN";

        confirmationTokenDetail1.setUser(user);

        Mockito.when(confirmationTokenRepo.findByConfirmationToken(confirmationToken))
                .thenReturn(confirmationTokenDetail1);
        Mockito.when(userRepo.findByEmail(confirmationTokenDetail1.getUser().getEmail()))
                .thenReturn(Optional.of(user));

        BankResponse response = confirmationService.activateAccount(confirmationToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(AccountUtils.ACCOUNT_EXISTS_CODE, response.getResponseCode());
        Assertions.assertEquals(AccountUtils.ACCOUNT_EXISTS_MESSAGE, response.getResponseMessage());

    }


    @Test
    void testRequestPasswordChange(){
        User user = new User();
        user.setEmail("dadsd");
        user.setAccountNumber("323");

        String token = "askjdasdikasld";

        when(userRepo.findByAccountNumber("323")).thenReturn(user);
        when(userRepo.existsByEmail("dadsd")).thenReturn(true);

        BankResponse result= new BankResponse();
        result.setResponseMessage(AccountUtils.CHECK_EMAIL);

        PasswordForgotReq passwordForgotReq = new PasswordForgotReq();
        passwordForgotReq.setEmail("dadsd");
        passwordForgotReq.setAccountNumber("323");

        ConfirmationTokenDetail confirmationTokenDetail = new ConfirmationTokenDetail();
        confirmationTokenDetail.setUser(user);
        confirmationTokenDetail.setConfirmationToken(token);

        Assertions.assertEquals(result,confirmationService.requestPasswordChange(passwordForgotReq));
    }
    @Test
    void testRequestPasswordChangeInvalid(){
        User user = new User();
        user.setEmail("dads");
        user.setAccountNumber("32");


        when(userRepo.existsByEmail("dadsd")).thenReturn(false);

        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseMessage(AccountUtils.INVALID_DETAILS);
        PasswordForgotReq passwordForgotReq = new PasswordForgotReq();
        passwordForgotReq.setEmail("dadsd");
        passwordForgotReq.setAccountNumber("323");

        Assertions.assertEquals(bankResponse,confirmationService.requestPasswordChange(passwordForgotReq));
    }
    @Test
    void testForgotPasswordSuccess(){
        User user = new User();
        user.setEmail("dads");
        user.setAccountNumber("323");

        String token = "askjdasdikasld";

        when(userRepo.existsByEmail("dads")).thenReturn(true);

        when(userRepo.findByAccountNumber("323")).thenReturn(user);

        when(confirmationTokenRepo.existsByConfirmationToken(token)).thenReturn(true);

        PasswordForgotEntity passwordForgotEntity = new PasswordForgotEntity();
        passwordForgotEntity.setNewPassword("eewe");
        passwordForgotEntity.setEmail("dads");
        passwordForgotEntity.setAccountNumber("323");
        passwordForgotEntity.setConfirmationToken(token);


        Assertions
                .assertEquals(AccountUtils.PASSWORD_CHANGED,confirmationService.forgotPassword(passwordForgotEntity).getResponseMessage());
    }
    @Test
    void testForgotPasswordSuccessInvaild(){
        User user = new User();
        user.setEmail("dads");
        user.setAccountNumber("323");

        String token = "askjdasdikasld";

        when(userRepo.existsByEmail("dads")).thenReturn(true);

        when(userRepo.findByAccountNumber("323")).thenReturn(user);

        when(confirmationTokenRepo.existsByConfirmationToken("askjdasdi")).thenReturn(false);

        PasswordForgotEntity passwordForgotEntity = new PasswordForgotEntity();
        passwordForgotEntity.setNewPassword("eewe");
        passwordForgotEntity.setEmail("dads");
        passwordForgotEntity.setAccountNumber("323");
        passwordForgotEntity.setConfirmationToken(token);


        Assertions
                .assertEquals(AccountUtils.INVALID_DETAILS,confirmationService.forgotPassword(passwordForgotEntity).getResponseMessage());
    }
    @Test
     void testGenerateToken(){
        User user = mock(User.class);

        String token = "askjdasdikasld";

        ConfirmationTokenDetail confirmationTokenDetail = new ConfirmationTokenDetail();
        confirmationTokenDetail.setUser(user);
        confirmationTokenDetail.setConfirmationToken(token);

        Assertions.assertEquals(user, confirmationTokenDetail.getUser());
        Assertions.assertEquals(token, confirmationTokenDetail.getConfirmationToken());

        Assertions.assertNotNull(confirmationTokenDetail);
    }
}
