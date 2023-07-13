package com.kanna.banco.confirmation;


import com.kanna.banco.dto.*;
import com.kanna.banco.dto.PasswordChangeEntity;
import com.kanna.banco.dto.PasswordForgotEntity;
import com.kanna.banco.dto.PasswordForgotReq;
import com.kanna.banco.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ConfirmationTokenController {


    private final ConfirmationService service;
    private final UserServiceImpl userService;

    @PostMapping("/register")
        public BankResponse registerAccount(@Valid @RequestBody UserReq userReq) throws MessagingException {
            return service.registerUser(userReq);
        }

    @GetMapping("/confirm/account")
    public BankResponse confirmUserAccount(@RequestParam("token") String confirmationToken) {
        return service.activateAccount(confirmationToken);
    }
    @GetMapping("/password/forgetPassword")
    public BankResponse forgetPasswordCHange(@Valid @RequestBody PasswordForgotEntity passwordForgotEntity){
        return service.forgotPassword(passwordForgotEntity);
    }
    @GetMapping("/password/forgetPasswordReq")
    public BankResponse forgePasswordReq(@RequestBody PasswordForgotReq passwordForgotReq){
        return service.requestPasswordChange(passwordForgotReq);
    }
    @GetMapping("/password/change")
    public BankResponse passwordChange(@Valid @RequestBody PasswordChangeEntity passwordChangeEntity){
        return userService.changePassword(passwordChangeEntity);
    }
}