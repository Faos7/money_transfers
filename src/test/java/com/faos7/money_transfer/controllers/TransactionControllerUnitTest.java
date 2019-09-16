package com.faos7.money_transfer.controllers;


import com.faos7.money_transfer.db.h2.tables.daos.MoneyTransactionDao;
import com.faos7.money_transfer.db.h2.tables.pojos.MoneyTransaction;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.faos7.money_transfer.util.JsonUtil.dataToJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionControllerUnitTest {
    @Mock
    private MoneyTransactionDao moneyTransactionDaoMock;
    @InjectMocks
    private TransactionController transactionController = new TransactionController(moneyTransactionDaoMock);

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
        moneyTransactionList = new ArrayList<>();
         moneyTransaction1 = new MoneyTransaction(1, "test", 1, 2, BigDecimal.ONE,
                LocalDateTime.of(2019, 2, 3, 5, 15));
        moneyTransaction2 = new MoneyTransaction(2, "test", 2, 3, BigDecimal.TEN,
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

//        given(TransactionController.moneyTransactionDao).willReturn(this.moneyTransactionDaoMock);
    }

    @Test
    public void testGetAllTransactionsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(moneyTransactionList), TransactionController.fetchAllTransactions.handle(request,response));
    }

    @Test
    public void testGetAllTransactionsNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null), TransactionController.fetchAllTransactions.handle(request,response));
    }

    @Test
    public void testGetTransactionByIdThatExistsAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("txId")).thenReturn("4");
        assertEquals(dataToJson(moneyTransaction4), TransactionController.fetchTransactionById.handle(request,response));
    }

    @Test
    public void testGetTransactionByIdThatDoesNotExistAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        when(request.params("txId")).thenReturn("5");
        assertEquals(dataToJson(null),TransactionController.fetchTransactionById.handle(request,response));
    }

    @Test
    public void testGetTransactionByIdThatExistsNotAcceptJson() throws Exception{
        when(request.params("txId")).thenReturn("4");
        assertEquals(dataToJson(null),TransactionController.fetchTransactionById.handle(request,response));
    }

    @Test
    public void testGetTransactionByIdThatDoesNotExistNotAcceptJson() throws Exception{
        when(request.params("txId")).thenReturn("5");
        assertEquals(dataToJson(null),TransactionController.fetchTransactionById.handle(request,response));
    }

    @Test
    public void testGetTransactionByIdNullAcceptJson() throws Exception{
        when(request.headers("Accept")).thenReturn("application/json");
        assertEquals(dataToJson(null),TransactionController.fetchTransactionById.handle(request,response));
    }

    @Test
    public void testGetTransactionByIdNullNotAcceptJson() throws Exception{
        assertEquals(dataToJson(null),TransactionController.fetchTransactionById.handle(request,response));
    }

}
