package com.kanna.banco.service;

import com.kanna.banco.dto.*;


public interface UserService {
    BankResponse balanceEnquiry(EnquiryReq enquiryReq);

    String nameEnquiry(EnquiryReq enquiryReq);

    BankResponse creditAccount(CreditDebitReq creditDebitReq);

    TransferResponse transferMoney(TransferMoney transferMoney);

}
