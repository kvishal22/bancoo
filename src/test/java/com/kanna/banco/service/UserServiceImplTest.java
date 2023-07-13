package com.kanna.banco.service;

import com.kanna.banco.dto.*;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.dto.PasswordChangeEntity;
import com.kanna.banco.statement.TransactionDto;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.utils.AccountUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private Emailservice emailservice;
    @Mock
    private TransactionService transactionService;
    private UserServiceImpl userService;
    @Mock
    private PasswordEncoder passwordEncoder;

    AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepo, emailservice, transactionService,passwordEncoder);
    }
    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
     void testBalanceEnquiryWhenAccountExists() {
        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("98392389");
        enquiryReq.setPassword("2344982384");
        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(1000));
        user.setAddress("njd");
        user.setFirstName("vishal");
        user.setLastName("kanna");
        user.setPassword("2344982384");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(true);
        when(passwordEncoder.matches(user.getPassword(),enquiryReq.getPassword())).thenReturn(true);
        when(userRepo.findByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(user);

        BankResponse result = userService.balanceEnquiry(enquiryReq);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
        verify(userRepo, times(1)).findByAccountNumber(enquiryReq.getAccountNumber());


        Assertions.assertNotNull(result);
        Assertions.assertEquals(AccountUtils.ACCOUNT_FOUND_CODE, result.getResponseCode());
       Assertions.assertEquals(AccountUtils.ACCOUNT_FOUND_MESSAGE, result.getResponseMessage());
       Assertions.assertNotNull(result.getAccountInfo());
       Assertions.assertEquals(enquiryReq.getAccountNumber(), result.getAccountInfo().getAccountNumber());
       Assertions.assertEquals("vishal kanna", result.getAccountInfo().getAccountName());
       Assertions.assertEquals(BigDecimal.valueOf(1000), result.getAccountInfo().getAccountBalance());
    }
     @Test
     void testBalanceEnquiryWhenAccountExistsButInCorrectPassword() {
         EnquiryReq enquiryReq = new EnquiryReq();
         enquiryReq.setAccountNumber("98392389");
         enquiryReq.setPassword("2344982384");
         User user = new User();
         user.setAccountBalance(BigDecimal.valueOf(1000));
         user.setAddress("njd");
         user.setFirstName("vishal");
         user.setLastName("kanna");
         user.setPassword("JHJHSJKDDAI");

         when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(false);
         when(passwordEncoder.matches(user.getPassword(),enquiryReq.getPassword())).thenReturn(false);

         BankResponse result = userService.balanceEnquiry(enquiryReq);

         verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
         verify(userRepo, times(1)).findByAccountNumber(enquiryReq.getAccountNumber());


         Assertions.assertNotNull(result);
         Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, result.getResponseCode());
         Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, result.getResponseMessage());
     }
    @Test
     void testBalanceEnquiryWhenAccountDoesNotExist() {

        // i was really happy for this :)))))
        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("9839238");
        enquiryReq.setPassword("32");

        User user = new User();
        user.setPassword("32");
        user.setAccountNumber("9839238");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(false);
        when(passwordEncoder.matches(enquiryReq.getPassword(),user.getPassword())).thenReturn(false);

        BankResponse result = userService.balanceEnquiry(enquiryReq);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, result.getResponseCode());
        Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, result.getResponseMessage());

    }

    @Test
     void nameExistsInEnquiryTest() {
        // you can do it ra kanna
        EnquiryReq enquiryReq = new EnquiryReq("902389208","foiajfoieu");

        User user = new User();
        user.setAccountNumber("902389208");
        user.setPassword("foiajfoieu");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(true);
        when(passwordEncoder.matches((enquiryReq.getPassword()), (user.getPassword()))).thenReturn(true);
        when(userRepo.findByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(user);

        BankResponse response = BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_HOLDER_NAME +"vishal kanna")
                .build();

        userService.nameEnquiry(enquiryReq);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
        verify(userRepo, times(1)).findByAccountNumber(enquiryReq.getAccountNumber());

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Account Holder Name: vishal kanna",response.getResponseMessage());

    }

    @Test
     void nameDoesNotExistinEnquiryTest() {
        // such a simple one bro
        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("98392389");
        enquiryReq.setPassword("dasdthgthtg");

        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(100));
        user.setFirstName("vishal");
        user.setLastName("kanna");
        user.setPassword("Das");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(false);
        when(passwordEncoder.matches(enquiryReq.getPassword(),user.getPassword())).thenReturn(false);

        BankResponse actualResponse = userService.nameEnquiry(enquiryReq);
        actualResponse.setResponseMessage(AccountUtils.INVALID_DETAILS);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
        verify(userRepo, times(1)).findByAccountNumber(enquiryReq.getAccountNumber());

        Assertions.assertEquals(AccountUtils.INVALID_DETAILS,actualResponse.getResponseMessage());
        Assertions.assertNotNull(actualResponse);
    }

    @Test
     void creditAccountShouldIncreaseBalanceIfAccountExists() {
        String accountNumber = "989889";
        BigDecimal initialBalance = BigDecimal.valueOf(1000);

        CreditDebitReq creditDebitReq = new CreditDebitReq();
        creditDebitReq.setAccountNumber(accountNumber);
        creditDebitReq.setAmount(BigDecimal.valueOf(1000));
        creditDebitReq.setPassword("Eweew");

        User user = new User();
        user.setAccountBalance(initialBalance);
        user.setAddress("njd");
        user.setFirstName("vishal");
        user.setLastName("kanna");
        user.setPassword("Eweew");

        when(userRepo.existsByAccountNumber(accountNumber)).thenReturn(true);
        when(passwordEncoder.matches(creditDebitReq.getPassword(), user.getPassword())).thenReturn(true);

        when(userRepo.findByAccountNumber(accountNumber)).thenReturn(user);

        BankResponse result = userService.creditAccount(creditDebitReq);

        verify(userRepo, times(1)).existsByAccountNumber(accountNumber);
        verify(userRepo, times(1)).findByAccountNumber(accountNumber);

       Assertions.assertNotNull(result);
       Assertions.assertEquals(AccountUtils.ACCOUNT_CREDITED_SUCCESS, result.getResponseCode());
       Assertions.assertEquals(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE, result.getResponseMessage());
       Assertions.assertEquals(accountNumber, result.getAccountInfo().getAccountNumber());
       Assertions.assertEquals("vishal kanna", result.getAccountInfo().getAccountName());
       Assertions.assertEquals(initialBalance.add(BigDecimal.valueOf(1000)), result.getAccountInfo().getAccountBalance());
    }

    @Test
     void creditAccountShouldReturnAccountNotExistIfAccountDoesNotExist() {
        String accountNumber = "989889";
        CreditDebitReq creditDebitReq = new CreditDebitReq();
        creditDebitReq.setAccountNumber(accountNumber);
        creditDebitReq.setAmount(BigDecimal.valueOf(1000));
        creditDebitReq.setPassword("98980");

        when(userRepo.existsByAccountNumber(accountNumber)).thenReturn(false);

        BankResponse result = userService.creditAccount(creditDebitReq);

        verify(userRepo, times(1)).existsByAccountNumber(accountNumber);
        verify(userRepo, times(1)).findByAccountNumber(accountNumber);

       Assertions.assertEquals(AccountUtils.INVALID_DETAILS, result.getResponseMessage());
    }

    @Test
     void transferMoneySuccessToToAccount(){

        BigDecimal transferAmount = BigDecimal.valueOf(500);

        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(1000));
        user.setEmail("vk@gmail");
        user.setFirstName("vishal");
        user.setLastName("kanna");
        user.setPassword("32442");
        user.setAccountNumber("2344324");

        User userTwo = new User();
        userTwo.setAccountBalance(BigDecimal.valueOf(2000));
        userTwo.setEmail("kg@gmail");
        userTwo.setFirstName("kishore");
        userTwo.setLastName("gillu");
        userTwo.setAccountNumber("343534");

        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(user.getAccountNumber());
        req.setToAccountNumber(userTwo.getAccountNumber());
        req.setAmount(transferAmount);
        req.setPassword("32442");

        when(userRepo.existsByAccountNumber(user.getAccountNumber())).thenReturn(true);
        when(userRepo.existsByAccountNumber(userTwo.getAccountNumber())).thenReturn(true);
        when(passwordEncoder.matches(user.getPassword(),req.getPassword())).thenReturn(true);

        when(userRepo.findByAccountNumber(user.getAccountNumber())).thenReturn(user);
        when(userRepo.findByAccountNumber(userTwo.getAccountNumber())).thenReturn(userTwo);

        TransferResponse response = userService.transferMoney(req);

        verify(userRepo,times(1)).findByAccountNumber(user.getAccountNumber());
        verify(userRepo,times(1)).findByAccountNumber(userTwo.getAccountNumber());
        verify(userRepo,times(1)).save(user);
        verify(userRepo,times(1)).save(userTwo);
        verify(emailservice, times(1)).sendEmailAlert(any(EmailDeets.class));
        verify(transactionService,times(2)).saveTransaction(any(TransactionDto.class));

       Assertions.assertNotNull(response);
       Assertions.assertEquals(AccountUtils.ACCOUNT_DEBITED_SUCCESS,response.getResponseCode());
       Assertions.assertEquals(AccountUtils.ACCOUNT_DEBITED_MESSAGE,response.getResponseMessage());
       Assertions.assertEquals(transferAmount,response.getDebitedAmount());
       Assertions.assertNotNull(response.getAccountInfo());
       Assertions.assertEquals(user.getAccountNumber(),response.getAccountInfo().getAccountNumber());
       Assertions.assertEquals("vishal kanna", response.getAccountInfo().getAccountName());
       Assertions.assertEquals(user.getAccountBalance(), response.getAccountInfo().getAccountBalance());
    }
    @Test
     void transferMoneyInsufficientBalance(){

        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(1000));
        user.setEmail("vk@gmail");
        user.setFirstName("vishal");
        user.setLastName("kanna");
        user.setAccountNumber("45353");
        user.setPassword("3242344");

        User userTwo = new User();
        userTwo.setAccountBalance(BigDecimal.valueOf(2000));
        userTwo.setEmail("kg@gmail");
        userTwo.setFirstName("kishore");
        userTwo.setLastName("gillu");
        userTwo.setAccountNumber("3242");

        BigDecimal transferAmount = BigDecimal.valueOf(2000);
        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(user.getAccountNumber());
        req.setToAccountNumber(userTwo.getAccountNumber());
        req.setAmount(transferAmount);

        when(userRepo.existsByAccountNumber(user.getAccountNumber())).thenReturn(true);
        when(userRepo.existsByAccountNumber(userTwo.getAccountNumber())).thenReturn(true);
        when(passwordEncoder.matches(req.getPassword(),user.getPassword())).thenReturn(true);

        when(userRepo.findByAccountNumber(user.getAccountNumber())).thenReturn(user);
        when(userRepo.findByAccountNumber(userTwo.getAccountNumber())).thenReturn(userTwo);

        TransferResponse response = userService.transferMoney(req);

        verify(userRepo,times(1)).findByAccountNumber(user.getAccountNumber());
        verify(userRepo,times(1)).findByAccountNumber(userTwo.getAccountNumber());

       Assertions.assertNotNull(response);
       Assertions.assertEquals(AccountUtils.INSUFFICIENT_BALANCE_CODE,response.getResponseCode());
       Assertions.assertEquals(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE,response.getResponseMessage());
       Assertions.assertNull(response.getAccountInfo());
    }
    @Test
     void transferMoneyFromAccountDoesNotExist(){
        String fromAccountNumber = "9344923848923";
        String toAccountNumber = "98349234040";

        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(fromAccountNumber);
        req.setToAccountNumber(toAccountNumber);
        req.setAmount(BigDecimal.valueOf(1000));

        when(userRepo.existsByAccountNumber(fromAccountNumber)).thenReturn(false);
        when(userRepo.existsByAccountNumber(toAccountNumber)).thenReturn(true);

        TransferResponse response = userService.transferMoney(req);

       Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE,response.getResponseCode());
       Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE,response.getResponseMessage());

    }
    @Test
     void transferMoneyToAccountDoesNotExist(){

        String fromAccountNumber = "9344923848923";
        String toAccountNumber = "98349234040";
        String password = "asdsdasd";

        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(fromAccountNumber);
        req.setToAccountNumber(toAccountNumber);
        req.setAmount(BigDecimal.valueOf(1000));
        req.setPassword(password);

        when(userRepo.existsByAccountNumber(fromAccountNumber)).thenReturn(true);
        when(userRepo.existsByAccountNumber(toAccountNumber)).thenReturn(false);


        TransferResponse response = userService.transferMoney(req);

       Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE,response.getResponseCode());
       Assertions.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE,response.getResponseMessage());
    }
     @Test
     void transferMoneyZeroBalance(){

         BigDecimal transferAmount = BigDecimal.valueOf(0);

         User user = new User();
         user.setAccountBalance(BigDecimal.valueOf(1000));
         user.setEmail("vk@gmail");
         user.setFirstName("vishal");
         user.setLastName("kanna");
         user.setAccountNumber("324234");

         User userTwo = new User();
         userTwo.setAccountBalance(BigDecimal.valueOf(2000));
         userTwo.setEmail("kg@gmail");
         userTwo.setFirstName("kishore");
         userTwo.setLastName("gillu");
         user.setAccountNumber("23423");


         TransferMoney req = new TransferMoney();
         req.setFromAccountNumber(user.getAccountNumber());
         req.setToAccountNumber(userTwo.getAccountNumber());
         req.setAmount(transferAmount);

         when(userRepo.existsByAccountNumber(user.getAccountNumber())).thenReturn(true);
         when(userRepo.existsByAccountNumber(userTwo.getAccountNumber())).thenReturn(true);
         when(passwordEncoder.matches(req.getPassword(),user.getPassword())).thenReturn(true);

         when(userRepo.findByAccountNumber(user.getAccountNumber())).thenReturn(user);
         when(userRepo.findByAccountNumber(userTwo.getAccountNumber())).thenReturn(userTwo);

         TransferResponse response = userService.transferMoney(req);

         verify(userRepo,times(1)).findByAccountNumber(user.getAccountNumber());
         verify(userRepo,times(1)).findByAccountNumber(userTwo.getAccountNumber());


         Assertions.assertNotNull(response);
         Assertions.assertEquals(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE,response.getResponseMessage());
         Assertions.assertNull(response.getAccountInfo());
     }
     @Test
     void creditZeroShouldNotIncreaseBalanceIfAccountExists() {
         String accountNumber = "989889";
         BigDecimal initialBalance = BigDecimal.valueOf(1000);

         CreditDebitReq creditDebitReq = new CreditDebitReq();
         creditDebitReq.setAccountNumber(accountNumber);
         creditDebitReq.setAmount(BigDecimal.valueOf(0));
         User user = new User();
         user.setAccountBalance(initialBalance);
         user.setAddress("njd");
         user.setFirstName("vishal");
         user.setLastName("kanna");

         when(userRepo.existsByAccountNumber(accountNumber)).thenReturn(true);
         when(passwordEncoder.matches(creditDebitReq.getPassword(),user.getPassword())).thenReturn(true);

         when(userRepo.findByAccountNumber(accountNumber)).thenReturn(user);

         BankResponse result = userService.creditAccount(creditDebitReq);

         verify(userRepo, times(1)).existsByAccountNumber(accountNumber);

         Assertions.assertNotNull(result);
         Assertions.assertEquals(AccountUtils.YOU_CANT_TRANSEFER_ZERO, result.getResponseMessage());
     }
     @Test
     void passwordChangeSuccessful(){
         User user = new User();
         user.setPassword("dasdasd");
         user.setAccountNumber("3244");
         user.setEmail("dadkjk@gmail.com");

         PasswordChangeEntity passwordChangeEntity = new PasswordChangeEntity();
         passwordChangeEntity.setNewPassword("NDJKSUIARFF");
         passwordChangeEntity.setCurrentPassword("dasdasd");
         passwordChangeEntity.setEmail("dadkjk@gmail.com");
         passwordChangeEntity.setAccountNumber("3244");

         when(userRepo.existsByEmail("dadkjk@gmail.com")).thenReturn(true);
         when(userRepo.findByAccountNumber("3244")).thenReturn(user);
         when(passwordEncoder.matches(passwordChangeEntity.getCurrentPassword(),user.getPassword())).thenReturn(true);

         BankResponse bankResponse = userService.changePassword(passwordChangeEntity);

         Assertions.assertEquals(AccountUtils.PASSWORD_CHANGED,bankResponse.getResponseMessage());

     }
     @Test
     void passwordChangeUnSuccessfulCurrentPasswordIncorrect(){
         User user = new User();
         user.setPassword("dasdasd");
         user.setAccountNumber("3244");
         user.setEmail("dadkjk@gmail.com");

         PasswordChangeEntity passwordChangeEntity = new PasswordChangeEntity();
         passwordChangeEntity.setNewPassword("NDJKSUIARFF");
         passwordChangeEntity.setCurrentPassword("dasdas");
         passwordChangeEntity.setEmail("dadkjk@gmail.com");
         passwordChangeEntity.setAccountNumber("3244");

         when(userRepo.existsByEmail("dadkjk@gmail.com")).thenReturn(true);
         when(userRepo.findByAccountNumber("3244")).thenReturn(user);
         when(passwordEncoder.matches(passwordChangeEntity.getCurrentPassword(),user.getPassword())).thenReturn(false);

         BankResponse bankResponse = userService.changePassword(passwordChangeEntity);

         Assertions.assertEquals(AccountUtils.INVALID_DETAILS,bankResponse.getResponseMessage());

     }
     @Test
     void passwordChangeUnSuccessfulAccountNotExist(){
         User user = new User();
         user.setPassword("dasdasd");
         user.setAccountNumber("3244");
         user.setEmail("dadkjk@gmail.com");

         PasswordChangeEntity passwordChangeEntity = new PasswordChangeEntity();
         passwordChangeEntity.setNewPassword("NDJKSUIARFF");
         passwordChangeEntity.setCurrentPassword("dasdas");
         passwordChangeEntity.setEmail("dadkjk@gmail.com");
         passwordChangeEntity.setAccountNumber("3244");

         when(userRepo.existsByEmail("dadkj@gmail.com")).thenReturn(false);
         when(userRepo.findByAccountNumber("324")).thenReturn(user);
         when(passwordEncoder.matches(passwordChangeEntity.getCurrentPassword(),user.getPassword())).thenReturn(true);

         BankResponse bankResponse = userService.changePassword(passwordChangeEntity);

         Assertions.assertEquals(AccountUtils.INVALID_DETAILS,bankResponse.getResponseMessage());

     }
 }

