package com.kanna.banco.controller;

import com.kanna.banco.dto.*;
import com.kanna.banco.entity.UserController;
import com.kanna.banco.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }
    @Test
     void testCheckBalanceMethod() throws Exception{
        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setPassword("32");
        enquiryReq.setAccountNumber("2344");

        BankResponse bankResponse = new BankResponse("200","account balance", AccountInfo.builder()
                .accountName("vishal kanna")
                .accountBalance(BigDecimal.valueOf(1000))
                .accountNumber("2344")
                .build());

        when(userService.balanceEnquiry(enquiryReq)).thenReturn(bankResponse);

        mockMvc.perform(get("/api/notanuser/balanceEnquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"2344\",\"password\":32}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).balanceEnquiry(enquiryReq);
        verifyNoMoreInteractions(userService);
    }
    @Test
     void testNameEnquiry() throws Exception {

        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("2344");
        enquiryReq.setPassword("324423");

        String expectedName = "vishal kanna";

        when(userService.nameEnquiry(enquiryReq))
                .thenReturn(expectedName);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/notanuser/nameEnquiry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"2344\",\"password\":324423}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedName))
                .andDo(print());

      verify(userService, times(1)).nameEnquiry(enquiryReq);
       verifyNoMoreInteractions(userService);
    }
    @Test
     void testDeposit() throws Exception {
        CreditDebitReq creditDebitReq = new CreditDebitReq();
        creditDebitReq.setAccountNumber("2344");
        creditDebitReq.setAmount(BigDecimal.valueOf(500));
        creditDebitReq.setPassword("32");
        BankResponse bankResponse = new BankResponse("200","account balance", AccountInfo.builder()
                .accountBalance(BigDecimal.valueOf(1000))
                .accountName("vishal kanna")
                .accountNumber("2344")
                .build());

        when(userService.creditAccount(creditDebitReq)).thenReturn(bankResponse);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/notanuser/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"2344\",\"amount\":500,\"password\":32}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).creditAccount(creditDebitReq);
        verifyNoMoreInteractions(userService);
    }

    @Test
     void testTransferCredit() throws Exception {
        TransferMoney transferMoney = new TransferMoney();
        transferMoney.setToAccountNumber("2345");
        transferMoney.setPassword("32");
        transferMoney.setFromAccountNumber("2344");
        transferMoney.setAmount(BigDecimal.valueOf(2));

        TransferResponse expectedResponse = new TransferResponse("200", "success",
                AccountInfo.builder()
                        .accountNumber("2344")
                        .accountName("vishal kanna")
                        .accountBalance(BigDecimal.valueOf(20))
                        .build(),
                BigDecimal.valueOf(200));

        when(userService.transferMoney(transferMoney)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/notanuser/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromAccountNumber\":\"2344\",\"toAccountNumber\":\"2345\",\"amount\":2,\"password\":32}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).transferMoney(transferMoney);
        verifyNoMoreInteractions(userService);
    }
}
// User user = new User(1,"vishal", "kanna","njd","tn","2344","vk","234",BigDecimal.valueOf(2),
//                "2344","s","@34234",
//                true, LocalDateTime.now(),LocalDateTime.now();