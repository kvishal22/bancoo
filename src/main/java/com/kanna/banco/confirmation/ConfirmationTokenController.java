package com.kanna.banco.confirmation;


import com.kanna.banco.dto.*;
import com.kanna.banco.password.PasswordForgotEntity;
import com.kanna.banco.password.PasswordForgotReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ConfirmationTokenController {


    private final ConfirmationService service;

    @PostMapping("/register")
        public String registerAccount(@RequestBody UserReq userReq){
            return service.registerUser(userReq);
        }

    @GetMapping("/confirm/account")
    public BankResponse confirmUserAccount(@RequestParam("token") String confirmationToken) {
        return service.activateAccount(confirmationToken);
    }
    @GetMapping("/password/forgetPassword")
    public BankResponse forgetPasswordCHange(@RequestBody PasswordForgotEntity passwordForgotEntity){
        return service.forgotPassword(passwordForgotEntity);
    }
    @GetMapping("/password/forgetPasswordReq")
    public String forgePasswordReq(@RequestBody PasswordForgotReq passwordForgotReq){
        return service.requestPasswordChange(passwordForgotReq);
    }
}