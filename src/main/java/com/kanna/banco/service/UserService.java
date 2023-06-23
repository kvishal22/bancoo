package com.kanna.banco.service;

import com.kanna.banco.dto.*;

import java.math.BigDecimal;

public interface UserService {
    BankResponse balanceEnquiry(EnquiryReq enquiryReq);

    String nameEnquiry(EnquiryReq enquiryReq);

    BankResponse creditAccount(CreditDebitReq creditDebitReq);

    TransferResponse transferMoney(TransferMoney transferMoney);

}
