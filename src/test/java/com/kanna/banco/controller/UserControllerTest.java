package com.kanna.banco.controller;

import com.kanna.banco.config.JwtService;
import com.kanna.banco.entity.UserController;
import com.kanna.banco.service.UserService;
import com.kanna.banco.service.UserServiceImpl;
import io.jsonwebtoken.JwtParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/*@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @Test
    public void testCheckBalanceMethod() throws Exception{
            mockMvc.perform(MockMvcRequestBuilders.get("/api/notanuser/balanceEnquiry"))
                    .andExpect(MockMvcResultMatchers.status().is(200));
    }
}
*/