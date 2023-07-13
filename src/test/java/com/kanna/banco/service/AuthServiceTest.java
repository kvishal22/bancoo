package com.kanna.banco.service;

import com.kanna.banco.auth.AuthService;
import com.kanna.banco.auth.AuthenticationReq;
import com.kanna.banco.auth.AuthenticationResponse;
import com.kanna.banco.config.JwtService;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.token.Token;
import com.kanna.banco.token.TokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private TokenRepo tokenRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepo,tokenRepo,
                jwtService,authenticationManager);
    }

    @Test
     void testAuthenticateValidCredentials(){
        String email = "test@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String jwtToken = "jwtToken";

        AuthenticationReq request = new AuthenticationReq(email, password);
        User user = new User();
        user.setEmail(email);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(jwtToken);


        AuthenticationResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals(jwtToken, response.getAccessToken());
       verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepo, times(1)).findByEmail(email);
        verify(jwtService, times(1)).generateToken(user);
        verify(tokenRepo, times(1)).save(any(Token.class));
        verify(tokenRepo, times(1)).findAllValidJwtTokenByUser(user.getId());
    }

    @Test
     void testAuthenticateInvalidCredentials() {
        String email = "test@example.com";
        String password = "password";

        AuthenticationReq request = new AuthenticationReq(email, password);
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(request));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepo, times(1)).findByEmail(email);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(tokenRepo);
    }
    @Test
    void revokeAllUserTokens(){
        User user = new User();
        user.setId(1);
           List<Token> validToken = new ArrayList<>();
            Token token1 = mock(Token.class);
            token1.setExpired(false);
            token1.setRevoked(false);
        validToken.add(token1);
        when(tokenRepo.findAllValidJwtTokenByUser(user.getId())).thenReturn(validToken);
        authService.revokeAllUserTokens(user);
        verify(token1).setExpired(true);
        verify(token1).setRevoked(true);
        verify(tokenRepo).saveAll(validToken);

    }
    @Test
    void notValidTokens(){
        User user = new User();
        user.setId(1);

        when(tokenRepo.findAllValidJwtTokenByUser(user.getId())).thenReturn(new ArrayList<>());

       verifyNoInteractions(tokenRepo);

        AuthService authService1 = mock(AuthService.class);
        authService1.revokeAllUserTokens(user);

      verify(authService1,times(1)).revokeAllUserTokens(user);
    }
    @Test
    void saveValidTokens(){
        User user = new User();
        var validToknes = tokenRepo
                .findAllValidJwtTokenByUser(user.getId());
        Token token = new Token();
        token.setRevoked(true);
        token.setExpired(true);
        assertNotNull(token);
        when(tokenRepo.saveAll(validToknes)).thenReturn(validToknes);
    }
}

