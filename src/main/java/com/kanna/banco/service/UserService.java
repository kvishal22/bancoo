package com.kanna.banco.service;

import com.kanna.banco.dto.*;


public interface UserService {
    BankResponse balanceEnquiry(EnquiryReq enquiryReq);

    BankResponse nameEnquiry(EnquiryReq enquiryReq);

    BankResponse creditAccount(CreditDebitReq creditDebitReq);

    TransferResponse transferMoney(TransferMoney transferMoney);

}
