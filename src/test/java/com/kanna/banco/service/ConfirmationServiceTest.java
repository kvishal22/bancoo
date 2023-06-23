package com.kanna.banco.service;

import com.kanna.banco.confirmation.ConfirmationService;
import com.kanna.banco.confirmation.ConfirmationToken;
import com.kanna.banco.confirmation.ConfirmationTokenRepo;
import com.kanna.banco.dto.UserReq;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmationServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private ConfirmationTokenRepo confirmationTokenRepo;
    @Mock
    private Emailservice emailservice;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ConfirmationService confirmationService;

    @Before
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
    public void testRegisterUserWhenUserDoesNotExist() throws Exception {
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

        Assert.assertNotNull(result);
        Assert.assertEquals("check your email to verify", result);
    }


    @Test
    public void userAlreadyExists() {
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

        Assert.assertNotNull(result);
        Assert.assertEquals("user already exists", result);
    }
}
