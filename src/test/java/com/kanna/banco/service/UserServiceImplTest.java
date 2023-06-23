package com.kanna.banco.service;

import com.kanna.banco.dto.*;
import com.kanna.banco.entity.User;
import com.kanna.banco.entity.UserRepo;
import com.kanna.banco.statement.TransactionDto;
import com.kanna.banco.statement.TransactionService;
import com.kanna.banco.utils.AccountUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Assert;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private Emailservice emailservice;
    @Mock
    private TransactionService transactionService;
    private UserServiceImpl userService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepo, emailservice, transactionService);
    }

    @Test
    public void testBalanceEnquiryWhenAccountExists() {
        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("98392389");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(true);

        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(1000));
        user.setAddress("njd");
        user.setFirstName("vishal");
        user.setLastName("kanna");

        when(userRepo.findByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(user);

        BankResponse result = userService.balanceEnquiry(enquiryReq);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
        verify(userRepo, times(1)).findByAccountNumber(enquiryReq.getAccountNumber());


        Assert.assertNotNull(result);
        Assert.assertEquals(AccountUtils.ACCOUNT_FOUND_CODE, result.getResponseCode());
        Assert.assertEquals(AccountUtils.ACCOUNT_FOUND_MESSAGE, result.getResponseMessage());
        Assert.assertNotNull(result.getAccountInfo());
        Assert.assertEquals(enquiryReq.getAccountNumber(), result.getAccountInfo().getAccountNumber());
        Assert.assertEquals("vishal kanna", result.getAccountInfo().getAccountName());
        Assert.assertEquals(BigDecimal.valueOf(1000), result.getAccountInfo().getAccountBalance());
    }

    @Test
    public void testBalanceEnquiryWhenAccountDoesNotExist() {

        // i was really happy for this :)))))

        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("9839238");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(false);

        BankResponse result = userService.balanceEnquiry(enquiryReq);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
        verify(userRepo, times(0)).findByAccountNumber(enquiryReq.getAccountNumber());

        Assert.assertNotNull(result);
        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, result.getResponseCode());
        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, result.getResponseMessage());


    }

    @Test
    public void nameExistsInEnquiryTest() {

        // you can do it ra kanna

        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("98392389");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(true);

        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(100));
        user.setFirstName("vishal");
        user.setLastName("kanna");

        when(userRepo.findByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(user);

        String result = userService.nameEnquiry(enquiryReq);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
        verify(userRepo, times(1)).findByAccountNumber(enquiryReq.getAccountNumber());


        Assert.assertNotNull(result);
        Assert.assertEquals("vishal kanna",result);

    }

    @Test
    public void nameDoesNotExistinEnquiryTest() {

        // such a simple one bro

        EnquiryReq enquiryReq = new EnquiryReq();
        enquiryReq.setAccountNumber("98392389");

        when(userRepo.existsByAccountNumber(enquiryReq.getAccountNumber())).thenReturn(false);

        String result = userService.nameEnquiry(enquiryReq);

        verify(userRepo, times(1)).existsByAccountNumber(enquiryReq.getAccountNumber());
        verify(userRepo, times(0)).findByAccountNumber(enquiryReq.getAccountNumber());

        Assert.assertNotNull(result);
        Assert.assertEquals("The account number does not exist",AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE);
    }

    @Test
    public void creditAccountShouldIncreaseBalanceIfAccountExists() {
        String accountNumber = "989889";
        BigDecimal initialBalance = BigDecimal.valueOf(1000);

        CreditDebitReq creditDebitReq = new CreditDebitReq();
        creditDebitReq.setAccountNumber(accountNumber);
        creditDebitReq.setAmount(BigDecimal.valueOf(1000));

        when(userRepo.existsByAccountNumber(accountNumber)).thenReturn(true);

        User user = new User();
        user.setAccountBalance(initialBalance);
        user.setAddress("njd");
        user.setFirstName("vishal");
        user.setLastName("kanna");

        when(userRepo.findByAccountNumber(accountNumber)).thenReturn(user);

        BankResponse result = userService.creditAccount(creditDebitReq);

        verify(userRepo, times(1)).existsByAccountNumber(accountNumber);
        verify(userRepo, times(1)).findByAccountNumber(accountNumber);

        Assert.assertNotNull(result);
        Assert.assertEquals(AccountUtils.ACCOUNT_CREDITED_SUCCESS, result.getResponseCode());
        Assert.assertEquals(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE, result.getResponseMessage());
        Assert.assertEquals(accountNumber, result.getAccountInfo().getAccountNumber());
        Assert.assertEquals("vishal kanna", result.getAccountInfo().getAccountName());
        Assert.assertEquals(initialBalance.add(BigDecimal.valueOf(1000)), result.getAccountInfo().getAccountBalance());
    }

    @Test
    public void creditAccountShouldReturnAccountNotExistIfAccountDoesNotExist() {
        String accountNumber = "989889";

        CreditDebitReq creditDebitReq = new CreditDebitReq();
        creditDebitReq.setAccountNumber(accountNumber);
        creditDebitReq.setAmount(BigDecimal.valueOf(1000));

        when(userRepo.existsByAccountNumber(accountNumber)).thenReturn(false);

        BankResponse result = userService.creditAccount(creditDebitReq);

        verify(userRepo, times(1)).existsByAccountNumber(accountNumber);
        verify(userRepo, times(0)).findByAccountNumber(accountNumber);

        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE, result.getResponseCode());
        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE, result.getResponseMessage());
    }

    @Test
    public void transferMoneySuccessToToAccount(){
        String fromAccountNumber = "9344923848923";
        String toAccountNumber = "98349234040";

        BigDecimal transferAmount = BigDecimal.valueOf(500);


        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(fromAccountNumber);
        req.setToAccountNumber(toAccountNumber);
        req.setAmount(transferAmount);


        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(1000));
        user.setEmail("vk@gmail");
        user.setFirstName("vishal");
        user.setLastName("kanna");

        User userTwo = new User();
        userTwo.setAccountBalance(BigDecimal.valueOf(2000));
        userTwo.setEmail("kg@gmail");
        userTwo.setFirstName("kishore");
        userTwo.setLastName("gillu");

        when(userRepo.existsByAccountNumber(fromAccountNumber)).thenReturn(true);
        when(userRepo.existsByAccountNumber(toAccountNumber)).thenReturn(true);

        when(userRepo.findByAccountNumber(fromAccountNumber)).thenReturn(user);
        when(userRepo.findByAccountNumber(toAccountNumber)).thenReturn(userTwo);

        TransferResponse response = userService.transferMoney(req);

        verify(userRepo,times(1)).existsByAccountNumber(fromAccountNumber);
        verify(userRepo,times(1)).existsByAccountNumber(toAccountNumber);
        verify(userRepo,times(1)).findByAccountNumber(fromAccountNumber);
        verify(userRepo,times(1)).findByAccountNumber(toAccountNumber);
        verify(userRepo,times(1)).save(user);
        verify(userRepo,times(1)).save(userTwo);
        verify(emailservice, times(1)).sendEmailAlert(any(EmailDeets.class));
        verify(transactionService,times(2)).saveTransaction(any(TransactionDto.class));

        Assert.assertNotNull(response);
        Assert.assertEquals(AccountUtils.ACCOUNT_DEBITED_SUCCESS,response.getResponseCode());
        Assert.assertEquals(AccountUtils.ACCOUNT_DEBITED_MESSAGE,response.getResponseMessage());
        Assert.assertEquals(transferAmount,response.getDebitedAmont());
        Assert.assertNotNull(response.getAccountInfo());
        Assert.assertEquals(fromAccountNumber,response.getAccountInfo().getAccountNumber());
        Assert.assertEquals("vishal kanna", response.getAccountInfo().getAccountName());
        Assert.assertEquals(user.getAccountBalance(), response.getAccountInfo().getAccountBalance());
    }
    @Test
    public void transferMoneyInsufficientBalance(){

        String fromAccountNumber = "9344923848923";
        String toAccountNumber = "98349234040";

        BigDecimal transferAmount = BigDecimal.valueOf(2000);


        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(fromAccountNumber);
        req.setToAccountNumber(toAccountNumber);
        req.setAmount(transferAmount);


        User user = new User();
        user.setAccountBalance(BigDecimal.valueOf(1000));
        user.setEmail("vk@gmail");
        user.setFirstName("vishal");
        user.setLastName("kanna");

        User userTwo = new User();
        userTwo.setAccountBalance(BigDecimal.valueOf(2000));
        userTwo.setEmail("kg@gmail");
        userTwo.setFirstName("kishore");
        userTwo.setLastName("gillu");

        when(userRepo.existsByAccountNumber(fromAccountNumber)).thenReturn(true);
        when(userRepo.existsByAccountNumber(toAccountNumber)).thenReturn(true);

        when(userRepo.findByAccountNumber(fromAccountNumber)).thenReturn(user);
        when(userRepo.findByAccountNumber(toAccountNumber)).thenReturn(userTwo);

        TransferResponse response = userService.transferMoney(req);

        verify(userRepo,times(1)).existsByAccountNumber(fromAccountNumber);
        verify(userRepo,times(1)).existsByAccountNumber(toAccountNumber);
        verify(userRepo,times(1)).findByAccountNumber(fromAccountNumber);
        verify(userRepo,times(1)).findByAccountNumber(toAccountNumber);
        verify(userRepo,times(0)).save(user);
        verify(userRepo,times(0)).save(userTwo);

        Assert.assertNotNull(response);
        Assert.assertEquals(AccountUtils.INSUFFICIENT_BALANCE_CODE,response.getResponseCode());
        Assert.assertEquals(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE,response.getResponseMessage());
        Assert.assertNull(response.getAccountInfo());
    }
    @Test
    public void transferMoneyFromAccountDoesNotExist(){
        String fromAccountNumber = "9344923848923";
        String toAccountNumber = "98349234040";

        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(fromAccountNumber);
        req.setToAccountNumber(toAccountNumber);
        req.setAmount(BigDecimal.valueOf(1000));

        when(userRepo.existsByAccountNumber(fromAccountNumber)).thenReturn(false);
        when(userRepo.existsByAccountNumber(toAccountNumber)).thenReturn(true);

        TransferResponse response = userService.transferMoney(req);

        verify(userRepo,times(1)).existsByAccountNumber(fromAccountNumber);
        verify(userRepo,times(1)).existsByAccountNumber(toAccountNumber);

        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE,response.getResponseCode());
        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE,response.getResponseMessage());

    }
    @Test
    public void transferMoneyToAccountDoesNotExist(){

        String fromAccountNumber = "9344923848923";
        String toAccountNumber = "98349234040";

        TransferMoney req = new TransferMoney();
        req.setFromAccountNumber(fromAccountNumber);
        req.setToAccountNumber(toAccountNumber);
        req.setAmount(BigDecimal.valueOf(1000));

        when(userRepo.existsByAccountNumber(fromAccountNumber)).thenReturn(true);
        when(userRepo.existsByAccountNumber(toAccountNumber)).thenReturn(false);

        TransferResponse response = userService.transferMoney(req);

        verify(userRepo,times(1)).existsByAccountNumber(fromAccountNumber);
        verify(userRepo,times(1)).existsByAccountNumber(toAccountNumber);

        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_CODE,response.getResponseCode());
        Assert.assertEquals(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE,response.getResponseMessage());
    }
    }

