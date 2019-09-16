package com.faos7.money_transfer.controllers;

import com.faos7.money_transfer.db.h2.tables.daos.AccountDao;
import com.faos7.money_transfer.db.h2.tables.daos.EndUserDao;
import com.faos7.money_transfer.db.h2.tables.daos.MoneyTransactionDao;
import com.faos7.money_transfer.db.h2.tables.pojos.Account;
import com.faos7.money_transfer.db.h2.tables.pojos.EndUser;
import com.faos7.money_transfer.db.h2.tables.pojos.MoneyTransaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.faos7.money_transfer.util.JsonUtil.dataToJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerUnitTest {
    @Mock
    EndUserDao endUserDaoMock;
    @Mock
    AccountDao accountDaoMock;
    @Mock
    MoneyTransactionDao moneyTransactionDaoMock;

    @InjectMocks
    AccountController accountController = new AccountController(endUserDaoMock,accountDaoMock,moneyTransactionDaoMock);

    List<EndUser> endUserList;
    EndUser endUser1;
    EndUser endUser2;

    List<Account> accountList;
    Account account1;
    Account account2;
    Account account3;

    List<MoneyTransaction> moneyTransactionList;
    MoneyTransaction moneyTransaction1;
    MoneyTransaction moneyTransaction2;
    MoneyTransaction moneyTransaction3;
    MoneyTransaction moneyTransaction4;

    @Mock
    private Request request;

    @Mock
    private Response response;


    @Before
    public void setUpTest() throws Exception {
        endUserList = new ArrayList<>();
        endUser1 = new EndUser(1,"John", "Brown", "+420777777777", "example@ex.com");
        endUser2 = new EndUser(2,"Martin", "Black", "+420888888888", "example.1@ex.com");
        endUserList.add(endUser1);
        endUserList.add(endUser2);
        given(endUserDaoMock.findAll()).willReturn(endUserList);
        given(endUserDaoMock.fetchOneById(1)).willReturn(endUser1);
        given(endUserDaoMock.fetchOneById(2)).willReturn(endUser2);

        accountList = new ArrayList<>();
        account1 = new Account(1,"account1",1, BigDecimal.ZERO);
        account2 = new Account(2, "account2", 1, BigDecimal.TEN);
        account3 = new Account(3, "account3", 2, BigDecimal.TEN);
        accountList.add(account1);
        accountList.add(account2);
        accountList.add(account3);
        given(accountDaoMock.findAll()).willReturn(accountList);
        given(accountDaoMock.fetchOneById(1)).willReturn(account1);
        given(accountDaoMock.fetchOneById(2)).willReturn(account2);
        given(accountDaoMock.fetchOneById(3)).willReturn(account3);
        doNothing().when(accountDaoMock).delete(any(Account.class));
        doNothing().when(accountDaoMock).update(any(Account.class));
        doNothing().when(accountDaoMock).insert(any(Account.class));

        moneyTransactionList = new ArrayList<>();
        moneyTransaction1 = new MoneyTransaction(1, "test", 2, 1, BigDecimal.ONE,
                LocalDateTime.of(2019, 2, 3, 5, 15));
        moneyTransaction2 = new MoneyTransaction(2, "test", 3, 2, BigDecimal.TEN,
                LocalDateTime.of(2019, 2, 3, 5, 17));
        moneyTransaction3 = new MoneyTransaction();
        moneyTransaction3.setId(3);
        moneyTransaction3.setToAccountId(2);
        moneyTransaction3.setAmount(BigDecimal.TEN);
        moneyTransaction3.setCreatedAt(LocalDateTime.of(2019, 2, 3, 5, 18));
        moneyTransaction4 = new MoneyTransaction();
        moneyTransaction4.setId(4);
        moneyTransaction4.setFromAccountId(2);
        moneyTransaction4.setAmount(BigDecimal.TEN);
        moneyTransaction4.setCreatedAt(LocalDateTime.of(2019, 2, 3, 5, 19));
        moneyTransactionList.add(moneyTransaction1);
        moneyTransactionList.add(moneyTransaction2);
        moneyTransactionList.add(moneyTransaction3);
        moneyTransactionList.add(moneyTransaction4);
        given(moneyTransactionDaoMock.findAll()).willReturn(moneyTransactionList);
        given(moneyTransactionDaoMock.fetchOneById(1)).willReturn(moneyTransaction1);
        given(moneyTransactionDaoMock.fetchOneById(2)).willReturn(moneyTransaction2);
        given(moneyTransactionDaoMock.fetchOneById(3)).willReturn(moneyTransaction3);
        given(moneyTransactionDaoMock.fetchOneById(4)).willReturn(moneyTransaction4);
        given(moneyTransactionDaoMock.fetchByFromAccountId(2)).willReturn(moneyTransactionList.stream()
                        .filter(tx -> tx.getFromAccountId() != null && tx.getFromAccountId() ==2)
                        .collect(Collectors.toList()));
        given(moneyTransactionDaoMock.fetchByToAccountId(2))
                .willReturn(moneyTransactionList.stream().filter(tx -> tx.getToAccountId()!=null &&  tx.getToAccountId()==2).collect(Collectors.toList()));
    }

    @Test
    public void testGetAllAccountsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(accountList), AccountController.fetchAllAccounts.handle(request,response));
    }

    @Test
    public void testGetAllAccountssNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),  AccountController.fetchAllAccounts.handle(request,response));
    }


    @Test
    public void testGetAccountByIdThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("1");
        assertEquals(dataToJson(account1), AccountController.fetchAccountById.handle(request, response));
    }

    @Test
    public void testGetAccountByIdThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null), AccountController.fetchAccountById.handle(request, response));
    }

    @Test
    public void testGetAccountByIdThatExistsNotAcceptJson() throws Exception {
        when(request.params("accountId")).thenReturn("1");
        assertEquals(dataToJson(null), AccountController.fetchAccountById.handle(request, response));
    }

    @Test
    public void testGetAccountByIdThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null),AccountController.fetchAccountById.handle(request, response));
    }

    @Test
    public void testGetAccountByIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),AccountController.fetchAccountById.handle(request, response));
    }

    @Test
    public void testGetAccountByIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),AccountController.fetchAccountById.handle(request, response));
    }

    @Test
    public void testGetAccountHolderByIdThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("1");
        assertEquals(dataToJson(endUser1), AccountController.fetchAccountHolder.handle(request, response));
    }

    @Test
    public void testGetAccountHolderByIdThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null), AccountController.fetchAccountHolder.handle(request, response));
    }

    @Test
    public void testGetAccountHolderByIdThatExistsNotAcceptJson() throws Exception {
        when(request.params("accountId")).thenReturn("1");
        assertEquals(dataToJson(null), AccountController.fetchAccountHolder.handle(request, response));
    }

    @Test
    public void testGetAccountHolderByIdThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null),AccountController.fetchAccountHolder.handle(request, response));
    }

    @Test
    public void testGetAccountHolderByIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),AccountController.fetchAccountHolder.handle(request, response));
    }

    @Test
    public void testGetAccountHolderByIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),AccountController.fetchAccountHolder.handle(request, response));
    }

    @Test
    public void testGetAccountTransactionsByIdThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("2");
        List<MoneyTransaction> expected = moneyTransactionList.stream()
                .filter(tx -> tx.getFromAccountId() != null && tx.getFromAccountId() ==2)
                .collect(Collectors.toList());
        expected.addAll(moneyTransactionList.stream()
                        .filter(tx -> tx.getToAccountId() != null && tx.getToAccountId() ==2)
                        .collect(Collectors.toList()));
        assertEquals(dataToJson(expected), AccountController.fetchAccountTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountTransactionsByIdThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null), AccountController.fetchAccountTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountTransactionsByIdThatExistsNotAcceptJson() throws Exception {
        when(request.params("accountId")).thenReturn("1");
        assertEquals(dataToJson(null), AccountController.fetchAccountTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountTransactionsByIdThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null),AccountController.fetchAccountTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountTransactionsByIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),AccountController.fetchAccountTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountTransactionsByIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),AccountController.fetchAccountTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountUpcomingTransactionsByIdThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("2");
        List<MoneyTransaction> expected = moneyTransactionList.stream()
                .filter(tx -> tx.getToAccountId() != null && tx.getToAccountId() ==2)
                .collect(Collectors.toList());
        assertEquals(dataToJson(expected), AccountController.fetchAccountUpcomingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountUpcomingTransactionsByIdThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null), AccountController.fetchAccountUpcomingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountUpcomingTransactionsByIdThatExistsNotAcceptJson() throws Exception {
        when(request.params("accountId")).thenReturn("1");
        assertEquals(dataToJson(null), AccountController.fetchAccountUpcomingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountUpcomingTransactionsByIdThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null),AccountController.fetchAccountUpcomingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountUpcomingTransactionsByIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),AccountController.fetchAccountUpcomingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountUpcomingTransactionsByIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),AccountController.fetchAccountUpcomingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountOutgoingTransactionsByIdThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("2");
        List<MoneyTransaction> expected = moneyTransactionList.stream()
                .filter(tx -> tx.getFromAccountId() != null && tx.getFromAccountId() ==2)
                .collect(Collectors.toList());
        assertEquals(dataToJson(expected), AccountController.fetchAccountOutgoingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountOutgoingTransactionsByIdThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null), AccountController.fetchAccountOutgoingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountOutgoingTransactionsByIdThatExistsNotAcceptJson() throws Exception {
        when(request.params("accountId")).thenReturn("1");
        assertEquals(dataToJson(null), AccountController.fetchAccountOutgoingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountOutgoingTransactionsByIdThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("accountId")).thenReturn("5");
        assertEquals(dataToJson(null),AccountController.fetchAccountOutgoingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountOutgoingTransactionsByIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),AccountController.fetchAccountOutgoingTransactions.handle(request, response));
    }

    @Test
    public void testGetAccountOutgoingTransactionsByIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),AccountController.fetchAccountOutgoingTransactions.handle(request, response));
    }

    @Test
    public void  testDeleteAccountWithExistingIdAndZeroBalance() throws Exception{
        when(request.params("accountId")).thenReturn("1");
        AccountController.deleteAccount.handle(request, response);
        verify(accountDaoMock, times(1)).fetchOneById(1);
        verify(accountDaoMock, times(1)).delete(account1);

    }

    @Test
    public void  testDeleteAccountWithExistingIdAndNonZeroBalance() throws Exception{
        when(request.params("accountId")).thenReturn("2");
        AccountController.deleteAccount.handle(request, response);
        verify(accountDaoMock, times(1)).fetchOneById(2);
        verify(accountDaoMock, times(0)).delete(any(Account.class));
    }

    @Test
    public void  testDeleteAccountWithNonExistingId() throws Exception{
        when(request.params("accountId")).thenReturn("5");
        AccountController.deleteAccount.handle(request, response);
        verify(accountDaoMock, times(1)).fetchOneById(5);
        verify(accountDaoMock, times(0)).delete(any(Account.class));
    }

    @Test
    public void  testDeleteAccountWithIdNull() throws Exception{
        AccountController.deleteAccount.handle(request, response);
        verify(accountDaoMock, times(1)).fetchOneById(anyInt());
        verify(accountDaoMock, times(0)).delete(any(Account.class));
    }


    @Test
    public void  testUpdateAccountWithExistingId() throws Exception{
        when(request.params("accountId")).thenReturn("1");
        Account account = new Account(1,"account5", 1, BigDecimal.TEN);
        when(request.body()).thenReturn(dataToJson(account));
        AccountController.updateAccount.handle(request, response);
        verify(accountDaoMock, times(1)).fetchOneById(1);
        verify(accountDaoMock, times(1)).update(account1);
    }

    @Test
    public void  testUpdateAccountWithNonExistingId() throws Exception{
        when(request.params("accountId")).thenReturn("5");
        Account account = new Account(5,"account5", 1, BigDecimal.TEN);
        when(request.body()).thenReturn(dataToJson(account));
        AccountController.updateAccount.handle(request, response);
        verify(accountDaoMock, times(1)).fetchOneById(5);
        verify(accountDaoMock, times(0)).update(any(Account.class));
    }

    @Test
    public void  testUpdateAccountWithNullId() throws Exception{
        Account account = new Account(5,"account5", 1, BigDecimal.TEN);
        when(request.body()).thenReturn(dataToJson(account));
        AccountController.updateAccount.handle(request, response);
        verify(accountDaoMock, times(0)).fetchOneById(any());
        verify(accountDaoMock, times(0)).update(any(Account.class));
    }

    @Test
    public void  testCreateAccountWithCorrectData() throws Exception{
        Account account = new Account(5,"account5", 1, BigDecimal.TEN);
        when(request.body()).thenReturn(dataToJson(account));
        AccountController.createAccount.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(1);
        verify(accountDaoMock, times(1)).insert(any(Account.class));
    }

    @Test
    public void  testCreateAccountWithNullName() throws Exception{
        Account account = new Account(5,null, 1, BigDecimal.TEN);
        when(request.body()).thenReturn(dataToJson(account));
        AccountController.createAccount.handle(request, response);
        verify(endUserDaoMock, times(0)).fetchOneById(anyInt());
        verify(accountDaoMock, times(0)).insert(any(Account.class));
    }

    @Test
    public void  testCreateAccountWithWrongHolderId() throws Exception{
        Account account = new Account(5,"account", 5, BigDecimal.TEN);
        when(request.body()).thenReturn(dataToJson(account));
        AccountController.createAccount.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(5);
        verify(accountDaoMock, times(0)).insert(any(Account.class));
    }

    @Test
    public void  testCreateAccountWithNullHolderId() throws Exception{
        Account account = new Account(5,"account", null, BigDecimal.TEN);
        when(request.body()).thenReturn(dataToJson(account));
        AccountController.createAccount.handle(request, response);
        verify(endUserDaoMock, times(0)).fetchOneById(anyInt());
        verify(accountDaoMock, times(0)).insert(any(Account.class));
    }
}
