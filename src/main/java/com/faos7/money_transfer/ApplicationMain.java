package com.faos7.money_transfer;

import com.faos7.money_transfer.controllers.AccountController;
import com.faos7.money_transfer.controllers.TransactionController;
import com.faos7.money_transfer.controllers.UserController;
import com.faos7.money_transfer.db.h2.tables.daos.EndUserDao;
import com.faos7.money_transfer.db.h2.tables.daos.MoneyTransactionDao;
import com.faos7.money_transfer.service.MoneyTransferService;
import com.faos7.money_transfer.service.impl.MoneyTransferServiceImpl;
import com.faos7.money_transfer.util.Filters;
import com.faos7.money_transfer.util.Path;
import spark.Request;
import spark.Response;
import spark.Route;
import com.faos7.money_transfer.db.h2.tables.daos.AccountDao;
import static spark.Spark.*;

public class ApplicationMain {

    public static AccountDao accountDao;
    public static EndUserDao endUserDao;
    public static MoneyTransactionDao moneyTransactionDao;
    public static MoneyTransferService moneyTransferService;

    public static void main(String[] args) throws Exception {

        accountDao = new AccountDao();
        endUserDao = new EndUserDao();
        moneyTransactionDao = new MoneyTransactionDao();
        moneyTransferService = new MoneyTransferServiceImpl();

        // Configure Spark
        port(4567);
        staticFiles.location("/public");
        staticFiles.expireTime(600L);

      // Set up before-filters (called before each get/post)
        before("*",                  Filters.addTrailingSlashes);
        before("*",                  Filters.handleLocaleChange);

        // Set up routes
        get(Path.Web.USERS,                         UserController.fetchAllUsers);
        get(Path.Web.ONE_USER,                      UserController.fetchUserById);
        get(Path.Web.ONE_USER_BY_EMAIL,             UserController.fetchUserByEmail);
        get(Path.Web.ONE_USER_BY_PHONE,             UserController.fetchUserByPhone);
        get(Path.Web.USER_ACCOUNTS,                 UserController.fetchUserAccounts);
        post(Path.Web.USER_CREATE,                  UserController.createUser);
        put(Path.Web.USER_UPDATE,                   UserController.updateUser);
        delete(Path.Web.USER_DELETE,                UserController.deleteUser);

        get(Path.Web.ACCOUNTS,                      AccountController.fetchAllAccounts);
        get(Path.Web.ONE_ACCOUNT,                   AccountController.fetchAccountById);
        get(Path.Web.ACCOUNT_HOLDER,                AccountController.fetchAccountHolder);
        get(Path.Web.ACCOUNT_TRANSACTIONS,          AccountController.fetchAccountTransactions);
        get(Path.Web.ACCOUNT_INCOMING_TRANSACTIONS, AccountController.fetchAccountUpcomingTransactions);
        get(Path.Web.ACCOUNT_OUTGOING_TRANSACTIONS, AccountController.fetchAccountOutgoingTransactions);
        post(Path.Web.ACCOUNT_CREATE,               AccountController.createAccount);
        put(Path.Web.ACCOUNT_UPDATE,                AccountController.updateAccount);
        delete(Path.Web.ACCOUNT_DELETE,             AccountController.deleteAccount);


        get(Path.Web.TRANSACTIONS,                  TransactionController.fetchAllTransactions);
        get(Path.Web.ONE_TRANSACTION,               TransactionController.fetchTransactionById);
        post(Path.Web.TRANSFER,                     TransactionController.transferMoney);
        post(Path.Web.WITHDRAW,                     TransactionController.withdrawMoney);
        post(Path.Web.DEPOSIT,                      TransactionController.depositMoney);
        //get("*",                     ViewUtil.notFound);



        //Set up after-filters (called after each get/post)
        after("*",                   Filters.addGzipHeader);

    }
}
