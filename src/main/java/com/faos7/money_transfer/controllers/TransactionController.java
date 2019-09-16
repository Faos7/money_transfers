package com.faos7.money_transfer.controllers;

import com.faos7.money_transfer.db.h2.tables.daos.MoneyTransactionDao;
import com.faos7.money_transfer.db.h2.tables.pojos.*;
import com.faos7.money_transfer.util.Path;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import static com.faos7.money_transfer.ApplicationMain.moneyTransferService;
import static com.faos7.money_transfer.util.JsonUtil.dataToJson;
import static com.faos7.money_transfer.util.RequestUtil.*;

public class TransactionController {

    private static MoneyTransactionDao moneyTransactionDao = new MoneyTransactionDao();

    public TransactionController(MoneyTransactionDao moneyTransactionDao) {
        TransactionController.moneyTransactionDao = moneyTransactionDao;
    }

    static Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public static Route fetchAllTransactions = (Request request, Response response) -> {
        if (clientAcceptsJson(request)) {
            return dataToJson(moneyTransactionDao.findAll());
        }
        return dataToJson(null);
    };

    public static Route fetchTransactionById = (Request request, Response response) -> {
        if (clientAcceptsJson(request)) {
            Integer txId = getParamTxId(request);
            if (txId != null) {
                return dataToJson(moneyTransactionDao.fetchOneById(txId));
            }
        }
        return dataToJson(null);
    };

    public static Route transferMoney = (Request request, Response response) -> {
        String data = Jsoup.parse(request.body()).text()
                .replaceAll(Path.Web.OK_PATTERN, "");
        try {
            logger.info("Parsed and Escaped data passed to new transaction = \n" + request.body());
            ObjectMapper objectMapper = new ObjectMapper();
            MoneyTransaction moneyTransaction = objectMapper.readValue(data, MoneyTransaction.class);
            logger.info("transaction after conversion for new = \n" + moneyTransaction.toString());
            MoneyTransaction newTransaction =  moneyTransferService.transferMoney(moneyTransaction.getFromAccountId(),
                    moneyTransaction.getToAccountId(), moneyTransaction.getAmount(), moneyTransaction.getDetails());
            if (newTransaction != null){
                response.status(200);
            } else {
                logger.info("null input fields!");
                response.status(400);
            }
        }  catch (Exception e) {
        e.printStackTrace();
        response.status(500);
    }
        return response.status();
    };

    public static Route depositMoney = (Request request, Response response) -> {
        String data = Jsoup.parse(request.body()).text()
                .replaceAll(Path.Web.OK_PATTERN, "");
        try {
            logger.info("Parsed and Escaped data passed to new transaction = \n" + request.body());
            ObjectMapper objectMapper = new ObjectMapper();
            MoneyTransaction moneyTransaction = objectMapper.readValue(data, MoneyTransaction.class);
            logger.info("transaction after conversion for new = \n" + moneyTransaction.toString());
            MoneyTransaction newTransaction = moneyTransferService.depositeMoney(moneyTransaction.getToAccountId(),
                    moneyTransaction.getAmount(), moneyTransaction.getDetails());
            if (newTransaction != null){
                response.status(200);
            } else {
                logger.info("null input fields!");
                response.status(400);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            response.status(500);
        }
        return response.status();
    };

    public static Route withdrawMoney = (Request request, Response response) ->{
        String data = Jsoup.parse(request.body()).text()
                .replaceAll(Path.Web.OK_PATTERN, "");
        try {
            logger.info("Parsed and Escaped data passed to new transaction = \n" + request.body());
            ObjectMapper objectMapper = new ObjectMapper();
            MoneyTransaction moneyTransaction = objectMapper.readValue(data, MoneyTransaction.class);
            logger.info("transaction after conversion for new = \n" + moneyTransaction.toString());
            MoneyTransaction newTransaction = moneyTransferService.withdrawMoney(moneyTransaction.getFromAccountId(),
                    moneyTransaction.getAmount(), moneyTransaction.getDetails());
            if (newTransaction != null){
                response.status(200);
            } else {
                logger.info("null input fields!");
                response.status(400);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            response.status(500);
        }
        return response.status();
    };
}
