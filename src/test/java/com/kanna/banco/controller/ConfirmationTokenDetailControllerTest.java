package com.kanna.banco.controller;

import com.kanna.banco.confirmation.ConfirmationService;
import com.kanna.banco.confirmation.ConfirmationTokenController;
import com.kanna.banco.dto.BankResponse;
import com.kanna.banco.dto.PasswordChangeEntity;
import com.kanna.banco.dto.UserReq;
import com.kanna.banco.service.UserServiceImpl;
import com.kanna.banco.utils.AccountUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


 class ConfirmationTokenDetailControllerTest {

    @InjectMocks
    private ConfirmationTokenController confirmationTokenController;

    @Mock
    private ConfirmationService confirmationService;
    @Mock
    private UserServiceImpl userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        confirmationTokenController = new ConfirmationTokenController(confirmationService,userService);
        mockMvc = MockMvcBuilders.standaloneSetup(confirmationTokenController).build();
    }
    @Test
     void testRegisterAccountNew() throws Exception {
        UserReq userReq = new UserReq("vishal", "kanna","njd","tn","vk","234","2344","@34234");

        String expectedResult = "check your email to verify";

       when(confirmationService.registerUser(Mockito.any(UserReq.class))).thenReturn(expectedResult);
        when(confirmationService.registerUser(userReq)).thenReturn(expectedResult);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"vishal\"," +
                                "\"lastName\":\"kanna\"," +
                                        "\"address\":\"njd\","+
                                "\"state\":\"tn\","+
                                "\"email\":\"vk\"," +
                                "\"phoneNumber\":\"234\","+
                                "\"alternateNumber\":\"2344\","+
                                "\"password\":\"@34234\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResult))
                .andDo(print());

        verify(confirmationService, times(1)).registerUser(userReq);
        verifyNoMoreInteractions(confirmationService);
    }
    @Test
     void testConfirmUserAccountShouldReturnBankResponse() throws Exception {
        String confirmationToken = "123456";
        BankResponse expectedResponse = new BankResponse();

        when(confirmationService.activateAccount(anyString())).thenReturn(expectedResponse);

        mockMvc.perform(get("/confirm/account")
                        .param("token", confirmationToken))
                .andExpect(status().isOk());
        verify(confirmationService,times(1)).activateAccount(confirmationToken);

    }
    @Test
     void testPasswordChange() throws Exception {

        BankResponse bankResponse = new BankResponse();
        bankResponse.setResponseMessage("password changed successfully");


        when(userService.changePassword(Mockito.any(PasswordChangeEntity.class)))
                .thenReturn(bankResponse);

        mockMvc.perform(get("/password/change")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newPassword\":\"vishal\"," +
                        "\"email\":\"vk\"," +
                        "\"accountNumber\":\"2344\","+
                        "\"currentPassword\":\"@34234\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json("{\"responseCode\":null,"+
                                "\"responseMessage\":\"password changed successfully\"," +
                                "\"accountInfo\":null}"))
             .andDo(print());

    }
}


