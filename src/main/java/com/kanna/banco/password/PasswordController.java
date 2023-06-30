package com.kanna.banco.password;


import com.kanna.banco.dto.BankResponse;
import com.kanna.banco.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/password/change")
@RequiredArgsConstructor
public class PasswordController {
    private final UserServiceImpl userService;
    @GetMapping
    public BankResponse passwordChange(@RequestBody PasswordChangeEntity passwordChangeEntity){
        return userService.changePassword(passwordChangeEntity);
    }
}
