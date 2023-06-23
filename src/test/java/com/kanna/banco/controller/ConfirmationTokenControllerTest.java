package com.kanna.banco.controller;

import com.kanna.banco.confirmation.ConfirmationService;
import com.kanna.banco.confirmation.ConfirmationTokenController;
import com.kanna.banco.dto.UserReq;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;



/*@RunWith(MockitoJUnitRunner.class)
public class ConfirmationTokenControllerTest {

    @InjectMocks
    private ConfirmationTokenController confirmationTokenController;

    @Mock
    private ConfirmationService confirmationService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(confirmationTokenController).build();
    }
    @Test
    public void testRegisterAccountNew() throws Exception {
        UserReq userReq = new UserReq();
        userReq.setFirstName("Kishore");
        userReq.setLastName("K");
        userReq.setEmail("kishoregillu@gmail.com");
        userReq.setAddress("njd");
        userReq.setPhoneNumber("9787617019");
        userReq.setAlternateNumber("234234234");
        userReq.setPassword("2344234");
        userReq.setState("TN");

        String expectedResult = "check your email to verify";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserReq> req = new HttpEntity<>(userReq, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/register",
                req, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(expectedResult, response.getBody());

    }

    @Test
    public void testRegisterAccountExisting() throws Exception {
        UserReq userReq = new UserReq();
        userReq.setFirstName("vishal");
        userReq.setLastName("kanna");
        userReq.setEmail("vishalk@gmail.com");
        userReq.setAddress("njd");
        userReq.setPhoneNumber("38472738443");
        userReq.setAlternateNumber("234234234");
        userReq.setPassword("2344234");
        userReq.setState("TN");

        String expectedResult = "user already exists";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserReq> req = new HttpEntity<>(userReq, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/register",
                req, String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(expectedResult, response.getBody());

    }
}
*/

