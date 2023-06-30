package com.kanna.banco.service;

import com.kanna.banco.confirmation.ConfirmationService;
import com.kanna.banco.confirmation.ConfirmationToken;
import com.kanna.banco.confirmation.ConfirmationTokenRepo;
import com.kanna.banco.dto.BankResponse;
import com.kanna.banco.dto.EmailDeets;
import com.kanna.banco.dto.UserReq;
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

import java.math.BigDecimal;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
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

    @Mock
    private ConfirmationToken confirmationToken;

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
     void testRegisterUserWhenUserDoesNotExist() throws Exception {
        UserReq userReq = new UserReq();
        userReq.setFirstName("vishal");
        userReq.setLastName("kanna");
        userReq.setEmail("vishalk@gmail.com");
        userReq.setPhoneNumber("972738443");

        when(userRepo.existsByEmail(userReq.getEmail())).thenReturn(false);
        when(userRepo.existsByPhoneNumber(userReq.getPhoneNumber())).thenReturn(false);

        when(userRepo.save(any(User.class))).thenReturn(new User());

        when(confirmationTokenRepo.save(any(ConfirmationToken.class))).thenReturn(new ConfirmationToken());

        String result = confirmationService.registerUser(userReq);

        verify(userRepo, times(0)).findByEmail(userReq.getEmail());
        verify(userRepo, times(1)).existsByPhoneNumber(userReq.getPhoneNumber());
        verify(userRepo, times(1)).existsByEmail(userReq.getEmail());

        verify(userRepo, times(1)).save(any(User.class));

        verify(confirmationTokenRepo, times(1)).save(any(ConfirmationToken.class));
        verify(emailservice).mailSend(any(SimpleMailMessage.class));

        Assertions.assertNotNull(result);
        Assertions.assertEquals("check your email to verify", result);
    }


    @Test
     void userAlreadyExistsByPhoneNumber() {
        UserReq userReq = new UserReq();
        userReq.setFirstName("vishal");
        userReq.setLastName("kanna");
        userReq.setEmail("vishalk@gmail.com");
        userReq.setPhoneNumber("9878987899");

        when(userRepo.existsByEmail(userReq.getEmail())).thenReturn(false);
        when(userRepo.existsByPhoneNumber(userReq.getPhoneNumber())).thenReturn(true);

        String result = confirmationService.registerUser(userReq);

        verify(userRepo, times(0)).findByEmail(userReq.getEmail());
        verify(userRepo, times(1)).existsByPhoneNumber(userReq.getPhoneNumber());
        verify(userRepo, times(1)).existsByEmail(userReq.getEmail());

        Assertions.assertNotNull(result);
        Assertions.assertEquals("user already exists", result);
    }
    @Test
     void userAlreadyExistsByEmail() {
        UserReq userReq = new UserReq();
        userReq.setFirstName("vishal");
        userReq.setLastName("kanna");
        userReq.setEmail("vishalk@gmail.com");
        userReq.setPhoneNumber("9878987899");

        when(userRepo.existsByEmail(userReq.getEmail())).thenReturn(true);
        when(userRepo.existsByPhoneNumber(userReq.getPhoneNumber())).thenReturn(false);

        String result = confirmationService.registerUser(userReq);

        verify(userRepo, times(0)).findByEmail(userReq.getEmail());
        verify(userRepo, times(0)).existsByPhoneNumber(userReq.getPhoneNumber());
        verify(userRepo, times(1)).existsByEmail(userReq.getEmail());

        Assertions.assertNotNull(result);
        Assertions.assertEquals("user already exists", result);
    }
    @Test
    void activateAccountFails(){
        String confirmationToken = "invalid_token";

        Mockito.when(confirmationTokenRepo.findByConfirmationToken(confirmationToken))
                .thenReturn(null);

        BankResponse response = confirmationService.activateAccount(confirmationToken);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(AccountUtils.ACCOUNT_EXISTS_CODE, response.getResponseCode());
        Assertions.assertEquals(AccountUtils.ACCOUNT_EXISTS_MESSAGE, response.getResponseMessage());
        Assertions.assertEquals(null, response.getAccountInfo());

        verify(userRepo, Mockito.never()).save(any(User.class));
        verify(emailservice, Mockito.never()).sendEmailAlert(any(EmailDeets.class));

    }
    @Test
    void activateAccountNotFails(){

        User user = new User();
        user.setEmail("dadsd");
        user.setEnabled(true);
        user.setAccountNumber("323");
        user.setAccountBalance(BigDecimal.valueOf(0));
        user.setFirstName("v");
        user.setLastName("k");
        user.setEmail("vk");

        ConfirmationToken confirmationToken1 = new ConfirmationToken();

        String confirmationToken = "DKJSNKJSN";
        confirmationToken1.setUser(user);

        Mockito.when(confirmationTokenRepo.findByConfirmationToken(confirmationToken))
                .thenReturn(confirmationToken1);
        Mockito.when(userRepo.findByEmail(confirmationToken1.getUser().getEmail()))
               .thenReturn(Optional.of(user));

        BankResponse response = confirmationService.activateAccount(confirmationToken);


        Assertions.assertNotNull(response);
        Assertions.assertEquals(AccountUtils.ACCOUNT_CREATION_SUCCESS, response.getResponseCode());
        Assertions.assertEquals(AccountUtils.ACCOUNT_CREATION_MESSAGE, response.getResponseMessage());
        Assertions.assertNotNull(response.getAccountInfo());
        Assertions.assertEquals(user.getAccountBalance(), response.getAccountInfo().getAccountBalance());
        Assertions.assertEquals(user.getAccountNumber(), response.getAccountInfo().getAccountNumber());
        Assertions.assertEquals(user.getFirstName() + " " + user.getLastName(), response.getAccountInfo().getAccountName());

        verify(userRepo).save(user);
        verify(emailservice).sendEmailAlert(any(EmailDeets.class));

    }
}
