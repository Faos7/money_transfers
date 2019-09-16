package com.faos7.money_transfer.controllers;

import com.faos7.money_transfer.db.h2.tables.daos.AccountDao;
import com.faos7.money_transfer.db.h2.tables.daos.EndUserDao;
import com.faos7.money_transfer.db.h2.tables.daos.MoneyTransactionDao;
import com.faos7.money_transfer.db.h2.tables.pojos.*;
import com.faos7.money_transfer.util.Path;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import java.math.BigDecimal;
import java.util.List;

import static com.faos7.money_transfer.util.JsonUtil.dataToJson;
import static com.faos7.money_transfer.util.RequestUtil.*;

public class AccountController {

    private static AccountDao accountDao = new AccountDao();
    private static EndUserDao endUserDao = new EndUserDao();
    private static MoneyTransactionDao moneyTransactionDao = new MoneyTransactionDao();

    public AccountController(EndUserDao endUserDao, AccountDao accountDao, MoneyTransactionDao moneyTransactionDao) {
        AccountController.accountDao = accountDao;
        AccountController.endUserDao = endUserDao;
        AccountController.moneyTransactionDao = moneyTransactionDao;
    }

    static Logger logger = LoggerFactory.getLogger(AccountController.class);

    public static Route fetchAllAccounts  = (Request request, Response response) -> {
        if (clientAcceptsJson(request)) {
            return dataToJson(accountDao.findAll());
        }
        return dataToJson(null);
    };

    public static Route fetchAccountHolder = (Request request, Response response) -> {
        if (clientAcceptsJson(request) ) {
            Integer accountId = getParamAccountId(request);
            if (accountId != null) {
                Account account = accountDao.fetchOneById(accountId);
                if (account!= null){
                    return dataToJson(endUserDao.fetchOneById(account.getEndUserId()));
                }
            }
        }
        return dataToJson(null);
    };

    public static Route fetchAccountById = (Request request, Response response) -> {
        if (clientAcceptsJson(request) ) {
            Integer accountId = getParamAccountId(request);
            if (accountId != null) {
                return dataToJson(accountDao.fetchOneById(accountId));
            }
        }
        return dataToJson(null);
    };

    public static Route fetchAccountTransactions = (Request request, Response response) -> {
        if (clientAcceptsJson(request) ) {
            Integer accountId = getParamAccountId(request);
            if (accountId != null) {
                Account account = accountDao.fetchOneById(accountId);
                if (account != null){
                    List<MoneyTransaction> transactions = moneyTransactionDao.fetchByFromAccountId(account.getId());
                    transactions.addAll(moneyTransactionDao.fetchByToAccountId(account.getId()));
                    return dataToJson(transactions);
                }
            }
        }
        return dataToJson(null);
    };

    public static Route fetchAccountUpcomingTransactions = (Request request, Response response) -> {
        if (clientAcceptsJson(request) ) {
            Integer accountId = getParamAccountId(request);
            if (accountId != null) {
                Account account = accountDao.fetchOneById(accountId);
                if (account != null) {
                    return dataToJson(moneyTransactionDao.fetchByToAccountId(account.getId()));
                }
            }
        }
        return dataToJson(null);
    };

    public static Route fetchAccountOutgoingTransactions = (Request request, Response response) -> {
        if (clientAcceptsJson(request) ) {
            Integer accountId = getParamAccountId(request);
            if (accountId != null) {
                Account account = accountDao.fetchOneById(accountId);
                if (account != null) {
                    return dataToJson(moneyTransactionDao.fetchByFromAccountId(account.getId()));
                }
            }
        }
        return dataToJson(null);
    };

    public static Route createAccount = (Request request, Response response) -> {
        String data = Jsoup.parse(request.body()).text()
                .replaceAll(Path.Web.OK_PATTERN, "");
        try {
            logger.info("Parsed and Escaped data passed to new account = \n" + request.body());
            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.readValue(data, Account.class);
            account.setBalance(BigDecimal.ZERO);
            logger.info("account after conversion for new = \n" + account.toString());

            if (account.getName() != null && account.getEndUserId() != null){
                EndUser holder = endUserDao.fetchOneById(account.getEndUserId());
                if (holder != null){
                    accountDao.insert(account);
                    response.status(200);
                } else {
                    logger.info("account holder not found!");
                    response.status(400);
                }
            } else {
                logger.info("null input fields!");
                response.status(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
        }

        return response.status();
    };


    public static Route updateAccount = (Request request, Response response) -> {
        String data = Jsoup.parse(request.body()).text()
                .replaceAll(Path.Web.OK_PATTERN, "");
        try {
            logger.info("Parsed and Escaped data passed to new Account = \n" + request.body());
            ObjectMapper objectMapper = new ObjectMapper();
            Account accountNew = objectMapper.readValue(data, Account.class);
            logger.info("account after conversion for new = \n" + accountNew.toString());
            Integer accountId = getParamAccountId(request);
            if (accountId!= null) {
                Account account = accountDao.fetchOneById(accountId);
                if (account != null) {
                    String name = accountNew.getName();
                    name = name == null ? account.getName() : name;
                    account.setName(name);
                    accountDao.update(account);
                    response.status(200);
                } else {
                       logger.info("account not found!");
                    response.status(400);
                }
            } else {
                logger.info("account not found!");
                response.status(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
        }
        return response.status();
    };

    public static Route deleteAccount = (Request request, Response response) -> {
        try {
            Integer id = getParamAccountId(request);
            Account account = accountDao.fetchOneById(id);
            if (account != null){
                if (account.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                    accountDao.delete(account);
                    response.status(200);
                } else {
                        logger.info("account not found!");
                    response.status(400);
                }
            } else {
                logger.info("account not found!");
                response.status(400);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
        }
        return response.status();
    };

}
