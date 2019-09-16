package com.faos7.money_transfer.controllers;

import com.faos7.money_transfer.db.h2.tables.pojos.Account;
import com.faos7.money_transfer.db.h2.tables.pojos.EndUser;
import com.faos7.money_transfer.db.h2.tables.daos.AccountDao;
import com.faos7.money_transfer.db.h2.tables.daos.EndUserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.faos7.money_transfer.util.JsonUtil.dataToJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerUnitTest {
    @Mock
    EndUserDao endUserDaoMock;
    @Mock
    AccountDao accountDaoMock;

    @InjectMocks
    UserController userController = new UserController(endUserDaoMock,accountDaoMock);

    List<EndUser> endUserList;
    EndUser endUser1;
    EndUser endUser2;
    EndUser endUser3;

    List<Account> accountList;
    Account account1;
    Account account2;
    Account account3;

    @Mock
    private Request request;

    @Mock
    private Response response;


    @Before
    public void setUpTest() throws Exception {
        endUserList = new ArrayList<>();
        endUser1 = new EndUser(1,"John", "Brown", "+420777777777", "example@ex.com");
        endUser2 = new EndUser(2,"Martin", "Black", "+420888888888", "example.1@ex.com");
        endUser3 = new EndUser(3,"Martin", "Brown", "+420999999999", "example.2@ex.com");
        endUserList.add(endUser1);
        endUserList.add(endUser2);
        given(endUserDaoMock.findAll()).willReturn(endUserList);
        given(endUserDaoMock.fetchOneById(1)).willReturn(endUser1);
        given(endUserDaoMock.fetchOneById(2)).willReturn(endUser2);
        given(endUserDaoMock.fetchOneById(3)).willReturn(endUser3);
        given(endUserDaoMock.fetchOneByEmail("example@ex.com")).willReturn(endUser1);
        given(endUserDaoMock.fetchOneByEmail("example.1@ex.com")).willReturn(endUser2);
        given(endUserDaoMock.fetchOneByPhone("+420777777777")).willReturn(endUser1);
        given(endUserDaoMock.fetchOneByPhone("+420888888888")).willReturn(endUser1);
//        when(endUserDaoMock.delete(endUser1)).then(endUserList.remove(endUser1));
        doNothing().when(endUserDaoMock).delete(any(EndUser.class));
        doNothing().when(endUserDaoMock).update(any(EndUser.class));
        doNothing().when(endUserDaoMock).insert(any(EndUser.class));
        accountList = new ArrayList<>();
        account1 = new Account(1,"account1",1, BigDecimal.ZERO);
        account2 = new Account(2, "account2", 1, BigDecimal.TEN);
        account3 = new Account(3, "account3", 2, BigDecimal.TEN);
        accountList.add(account1);
        accountList.add(account2);
        accountList.add(account3);
        given(accountDaoMock.fetchByEndUserId(1)).willReturn(accountList.stream().filter(acc -> acc.getEndUserId() != null && acc.getEndUserId()  ==1).collect(Collectors.toList()));
        given(accountDaoMock.fetchByEndUserId(2)).willReturn(accountList.stream().filter(acc -> acc.getEndUserId() != null && acc.getEndUserId()  ==2).collect(Collectors.toList()));
        given(accountDaoMock.fetchByEndUserId(3)).willReturn(accountList.stream().filter(acc -> acc.getEndUserId() != null && acc.getEndUserId()  ==3).collect(Collectors.toList()));
    }

    @Test
    public void testGetAllUsersAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(endUserList), UserController.fetchAllUsers.handle(request,response));
    }

    @Test
    public void testGetAllUsersNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null), UserController.fetchAllUsers.handle(request,response));
    }


    @Test
    public void testGetUserByIdThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("userId")).thenReturn("1");
        assertEquals(dataToJson(endUser1), UserController.fetchUserById.handle(request, response));
    }

    @Test
    public void testGetUserByIdThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("userId")).thenReturn("5");
        assertEquals(dataToJson(null), UserController.fetchUserById.handle(request, response));
    }

    @Test
    public void testGetUserByIdThatExistsNotAcceptJson() throws Exception {
        when(request.params("userId")).thenReturn("1");
        assertEquals(dataToJson(null), UserController.fetchUserById.handle(request, response));
    }

    @Test
    public void testGetUserByIdThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("userId")).thenReturn("5");
        assertEquals(dataToJson(null),UserController.fetchUserById.handle(request,response));
    }

    @Test
    public void testGetUserByIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),UserController.fetchUserById.handle(request,response));
    }

    @Test
    public void testGetUserByIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),UserController.fetchUserById.handle(request,response));
    }

    @Test
    public void testGetUserByPhoneThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("phone")).thenReturn("+420777777777");
        assertEquals(dataToJson(endUser1), UserController.fetchUserByPhone.handle(request, response));
    }

    @Test
    public void testGetUserByPhoneThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("phone")).thenReturn("420777777778");
        assertEquals(dataToJson(null), UserController.fetchUserByPhone.handle(request, response));
    }

    @Test
    public void testGetUserByPhoneThatExistsNotAcceptJson() throws Exception {
        when(request.params("phone")).thenReturn("+420777777777");
        assertEquals(dataToJson(null), UserController.fetchUserByPhone.handle(request, response));
    }

    @Test
    public void testGetUserByPhoneThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("phone")).thenReturn("+420777777778");
        assertEquals(dataToJson(null),UserController.fetchUserByPhone.handle(request,response));
    }

    @Test
    public void testGetUserByPhoneNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),UserController.fetchUserByPhone.handle(request,response));
    }

    @Test
    public void testGetUserByPhoneNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),UserController.fetchUserByPhone.handle(request,response));
    }

    @Test
    public void testGetUserByEmailThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("email")).thenReturn("example@ex.com");
        assertEquals(dataToJson(endUser1), UserController.fetchUserByEmail.handle(request, response));
    }

    @Test
    public void testGetUserByEmailThatDoesNotExistAcceptJson() throws Exception {
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("email")).thenReturn("example.2@ex.com");
        assertEquals(dataToJson(null), UserController.fetchUserByEmail.handle(request, response));
    }

    @Test
    public void testGetUserByEmailThatExistsNotAcceptJson() throws Exception {
        when(request.params("email")).thenReturn("example@ex.com");
        assertEquals(dataToJson(null), UserController.fetchUserByEmail.handle(request, response));
    }

    @Test
    public void testGetUserByEmailThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("email")).thenReturn("example.2@ex.com");
        assertEquals(dataToJson(null),UserController.fetchUserByEmail.handle(request,response));
    }

    @Test
    public void testGetUserByEmailNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),UserController.fetchUserByPhone.handle(request,response));
    }

    @Test
    public void testGetUserByEmailNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),UserController.fetchUserByPhone.handle(request,response));
    }

    @Test
    public void testGetUserAccountsWithExistingUserIdAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("userId")).thenReturn("1");
        assertEquals(dataToJson(accountList.stream().filter(acc -> acc.getEndUserId()==1).collect(Collectors.toList())),UserController.fetchUserAccounts.handle(request, response));
    }

    @Test
    public void testGetUserAccountsWithExistingUserIdNotAcceptJson() throws Exception{
        when(request.params("userId")).thenReturn("1");
        assertEquals(dataToJson(null),UserController.fetchUserAccounts.handle(request, response));
    }

    @Test
    public void testGetUserAccountsWithNonExistingUserIdAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("userId")).thenReturn("5");
        assertEquals(dataToJson(null),UserController.fetchUserAccounts.handle(request, response)); // fails
    }

    @Test
    public void testGetUserAccountsWithNonExistingUserIdNotAcceptJson() throws Exception{
        when(request.params("userId")).thenReturn("5");
        assertEquals(dataToJson(null),UserController.fetchUserAccounts.handle(request, response));
    }

    @Test
    public void testGetUserAccountsWithUserIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),UserController.fetchUserAccounts.handle(request, response));
    }

    @Test
    public void testGetUserAccountsWithUserIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),UserController.fetchUserAccounts.handle(request, response));
    }

    @Test
    public void  testDeleteUserWithExistingIdAndNoAccounts() throws Exception{
        when(request.params("userId")).thenReturn("3");
        UserController.deleteUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(3);
        verify(accountDaoMock, times(1)).fetchByEndUserId(3);
        verify(endUserDaoMock, times(1)).delete(endUser3);
    }

    @Test
    public void  testDeleteUserWithExistingIdAndSomeAccounts() throws Exception{
        when(request.params("userId")).thenReturn("1");
        UserController.deleteUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(1);
        verify(accountDaoMock, times(1)).fetchByEndUserId(1);
        verify(endUserDaoMock, times(0)).delete(any(EndUser.class));
    }

    @Test
    public void  testDeleteUserWithNonExistingId() throws Exception{
        when(request.params("userId")).thenReturn("5");
        UserController.deleteUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(5);
        verify(accountDaoMock, times(0)).fetchByEndUserId(any());
        verify(endUserDaoMock, times(0)).delete(any(EndUser.class));
    }

    @Test
    public void  testDeleteUserWithIdNull() throws Exception{
        UserController.deleteUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(any());
        verify(accountDaoMock, times(0)).fetchByEndUserId(any());
        verify(endUserDaoMock, times(0)).delete(any(EndUser.class));
    }

    @Test
    public void  testUpdateUserWithExistingIdAndCorrectData() throws Exception{
        when(request.params("userId")).thenReturn("1");
        EndUser endUser1New = new EndUser(1,"Sarah", "Red", "+420777777787", "sarah.example@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.updateUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(1);
        verify(endUserDaoMock, times(1)).update(endUser1);
    }

    @Test
    public void  testUpdateUserWithExistingIdAndWrongData() throws Exception{
        when(request.params("userId")).thenReturn("1");
        EndUser endUser1New = new EndUser(1,"Sarah", "Red", "+420888888888", "example.1@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.updateUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(1);
        verify(endUserDaoMock, times(1)).update(endUser1);
    }

    @Test
    public void  testUpdateUserWithNonExistingIdAndCorrectData() throws Exception{
        when(request.params("userId")).thenReturn("5");
        EndUser endUser1New = new EndUser(5,"Sarah", "Red", "+420777777787", "sarah.example@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.updateUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(5);
        verify(endUserDaoMock, times(0)).update(any(EndUser.class));
    }

    @Test
    public void  testUpdateUserWithNonExistingIdAndWrongData() throws Exception{
        when(request.params("userId")).thenReturn("5");
        EndUser endUser1New = new EndUser(5,"Sarah", "Red", "+420888888888", "example.1@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.updateUser.handle(request, response);
        verify(endUserDaoMock, times(1)).fetchOneById(5);
        verify(endUserDaoMock, times(0)).update(any(EndUser.class));
    }

    @Test
    public void  testUpdateUserWithNullIdAndCorrectData() throws Exception{
        EndUser endUser1New = new EndUser(5,"Sarah", "Red", "+420777777787", "sarah.example@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.updateUser.handle(request, response);
        verify(endUserDaoMock, times(0)).fetchOneById(any());
        verify(endUserDaoMock, times(0)).update(any(EndUser.class));
    }

    @Test
    public void  testUpdateUserWithNullIdAndWrongData() throws Exception{
        EndUser endUser1New = new EndUser(5,"Sarah", "Red", "+420888888888", "example.1@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.updateUser.handle(request, response);
        verify(endUserDaoMock, times(0)).fetchOneById(any());
        verify(endUserDaoMock, times(0)).update(any(EndUser.class));
    }

    @Test
    public void  testCreateUserWithCorrectData() throws Exception{
        EndUser endUser1New = new EndUser(1,"Sarah", "Red", "+420777777787", "sarah.example@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.createUser.handle(request, response);
        verify(endUserDaoMock, times(1)).findAll();
        verify(endUserDaoMock, times(1)).insert(any(EndUser.class));
    }

    @Test
    public void  testCreateUserWithNonUnicFields() throws Exception{
        EndUser endUser1New = new EndUser(1,"Sarah", "Red", "+420888888888", "example.1@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.createUser.handle(request, response);
        verify(endUserDaoMock, times(1)).findAll();
        verify(endUserDaoMock, times(0)).insert(any(EndUser.class));
    }

    @Test
    public void  testCreateUserWithNullFirstName() throws Exception{
        EndUser endUser1New = new EndUser(1,null, "Red", "+420888888888", "example.1@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.createUser.handle(request, response);
        verify(endUserDaoMock, times(0)).findAll();
        verify(endUserDaoMock, times(0)).insert(any(EndUser.class));
    }

    @Test
    public void  testCreateUserWithNullLastName() throws Exception{
        EndUser endUser1New = new EndUser(1,"Sarah", null, "+420888888888", "example.1@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.createUser.handle(request, response);
        verify(endUserDaoMock, times(0)).findAll();
        verify(endUserDaoMock, times(0)).insert(any(EndUser.class));
    }

    @Test
    public void  testCreateUserWithNullPhone() throws Exception{
        EndUser endUser1New = new EndUser(1,"Sarah", "Red", null, "example.1@ex.com");
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.createUser.handle(request, response);
        verify(endUserDaoMock, times(0)).findAll();
        verify(endUserDaoMock, times(0)).insert(any(EndUser.class));
    }

    @Test
    public void  testCreateUserWithNullEmail() throws Exception{
        EndUser endUser1New = new EndUser(1,"Sarah", "Red", "+420888888888", null);
        when(request.body()).thenReturn(dataToJson(endUser1New));
        UserController.createUser.handle(request, response);
        verify(endUserDaoMock, times(0)).findAll();
        verify(endUserDaoMock, times(0)).insert(any(EndUser.class));
    }
}
