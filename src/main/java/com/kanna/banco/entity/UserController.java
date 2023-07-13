package com.kanna.banco.entity;

import com.kanna.banco.dto.*;
import com.kanna.banco.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;



@RequiredArgsConstructor
@RestController
@RequestMapping("api/notanuser")
public class UserController {

    private final UserService service;

    @GetMapping("/balanceEnquiry")
    public BankResponse checkBalance(@RequestBody EnquiryReq enquiryReq){
        return service.balanceEnquiry(enquiryReq);
    }
    @GetMapping("/nameEnquiry")
    public BankResponse nameEnquiry(@RequestBody EnquiryReq enquiryReq){
        return service.nameEnquiry(enquiryReq);
    }
    @PostMapping("/deposit")
    public BankResponse credit(@RequestBody CreditDebitReq creditDebitReq){
        return service.creditAccount(creditDebitReq);
    }
    @PostMapping("/transfer")
    public TransferResponse transferCredit(@RequestBody TransferMoney transferMoney){
        return service.transferMoney(transferMoney);
    }

    }

