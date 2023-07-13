package com.kanna.banco.merchantpay;

import com.kanna.banco.dto.BankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping("/generateOtp")
    public BankResponse payToMerchant(@RequestBody MerchantDto req) throws MessagingException { //lmao you didnt put request body for more than 2 hours
        return merchantService.payToMerchant(req);//you are such a clown
    }
    @GetMapping("/payToMerchant")
    public BankResponse pay(@RequestParam String otp, @RequestParam String email){
        return merchantService.payMerchantVerify(otp, email);
    }
}
