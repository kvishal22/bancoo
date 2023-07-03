package com.kanna.banco;

import com.kanna.banco.confirmation.ConfirmationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BancoApplicationTests {

    @Autowired
    private ConfirmationService service;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(service);
    }

}
