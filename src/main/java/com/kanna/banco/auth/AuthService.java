package com.kanna.banco.auth;

import com.kanna.banco.config.JwtService;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.token.Token;
import com.kanna.banco.token.TokenRepo;
import com.kanna.banco.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


        public AuthenticationResponse authenticate (AuthenticationReq request) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
                var user = userRepo.findByEmail(request.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("email does not exist"));
                var jwtToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, jwtToken);
                return AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .build();
            }
        private void saveUserToken (User user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .jwtToken(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepo.save(token);
    }
        //private
        public void revokeAllUserTokens (User user){
        var validUserTokens = tokenRepo.findAllValidJwtTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }

}


