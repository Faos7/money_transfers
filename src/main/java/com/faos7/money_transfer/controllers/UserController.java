package com.faos7.money_transfer.controllers;

import com.faos7.money_transfer.db.h2.tables.daos.AccountDao;
import com.faos7.money_transfer.db.h2.tables.daos.EndUserDao;
import com.faos7.money_transfer.db.h2.tables.pojos.Account;
import com.faos7.money_transfer.db.h2.tables.pojos.EndUser;
import com.faos7.money_transfer.util.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import static com.faos7.money_transfer.util.JsonUtil.dataToJson;
import static com.faos7.money_transfer.util.RequestUtil.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.util.List;

public class UserController {

    private static EndUserDao endUserDao = new EndUserDao();
    private static AccountDao accountDao = new AccountDao();

    public UserController(EndUserDao endUserDao, AccountDao accountDao) {
        UserController.accountDao = accountDao;
        UserController.endUserDao = endUserDao;
    }

    static Logger logger = LoggerFactory.getLogger(UserController.class);

    public static Route fetchAllUsers = (Request request, Response response) -> {
        if (clientAcceptsJson(request)) {
            return dataToJson(endUserDao.findAll());
        }
        return dataToJson(null);
    };

    public static Route fetchUserById = (Request request, Response response) ->{
        if (clientAcceptsJson(request)) {
            Integer userId = getParamUserId(request);
            if (userId != null) {
                return dataToJson(endUserDao.fetchOneById(userId));
            }
        }
        return dataToJson(null);
    };

    public static Route fetchUserByPhone = (Request request, Response response) ->{
        if (clientAcceptsJson(request)){
            return dataToJson(endUserDao.fetchOneByPhone(getParamPhone(request)));
        }
        return dataToJson(null);
    };

    public static Route fetchUserByEmail = (Request request, Response response) ->{
        if (clientAcceptsJson(request)){
            return dataToJson(endUserDao.fetchOneByEmail(getParamEmail(request)));
        }
        return dataToJson(null);
    };

    public static Route fetchUserAccounts =  (Request request, Response response) ->{
        if (clientAcceptsJson(request)){
            Integer id = getParamUserId(request);
            if (id!=null) {
                EndUser endUser = endUserDao.fetchOneById(id);
                if (endUser != null) {
                    return dataToJson(accountDao.fetchByEndUserId(id));
                }
            }
        }
        return dataToJson(null);
    };

    public static Route createUser = (Request request, Response response) -> {
        String data = Jsoup.parse(request.body()).text()
                .replaceAll(Path.Web.OK_PATTERN, "");
        try {
            logger.info("Parsed and Escaped data passed to new EndUser = \n" + request.body());
            ObjectMapper objectMapper = new ObjectMapper();
            EndUser endUser = objectMapper.readValue(data, EndUser.class);
            logger.info("endUser after conversion for new = \n" + endUser.toString());
            if (endUser.getPhone() != null && endUser.getEmail() != null
            && endUser.getFirstName() != null && endUser.getLastName() != null) {
                //I fetch all users rather fetching single one by unic fields in order to reduce calls to db
                List<EndUser> endUserList = endUserDao.findAll();
                boolean phonePresent = endUserList.stream().anyMatch(user -> user.getPhone().equals(endUser.getPhone()));
                boolean emailPresent = endUserList.stream().anyMatch(user -> user.getEmail().equals(endUser.getEmail()));
                if (!emailPresent && !phonePresent) {
                    endUserDao.insert(endUser);
                    response.status(200);
                } else {
                    logger.info("non unic fields that should be unic");
                    response.status(400);
                }
            } else {
                logger.info("null fields that should be unic");
                response.status(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
        }

        return response.status();
    };

    public static Route updateUser = (Request request, Response response) -> {
        String data = Jsoup.parse(request.body()).text()
                .replaceAll(Path.Web.OK_PATTERN, "");
        try {
            logger.info("Parsed and Escaped data passed to new EndUser = \n" + request.body());
            ObjectMapper objectMapper = new ObjectMapper();
            EndUser endUserNew = objectMapper.readValue(data, EndUser.class);
            logger.info("endUser after conversion for new = \n" + endUserNew.toString());
            Integer userId = getParamUserId(request);
            if (userId!= null) {
                EndUser endUser = endUserDao.fetchOneById(userId);
                if (endUser != null) {
                    String newPhone = endUserNew.getPhone();
                    String newEmail = endUserNew.getEmail();
                    //I fetch all users rather fetching single one by unic fields in order to reduce calls to db
                    List<EndUser> endUserList = endUserDao.findAll();
                    endUserList.remove(endUser);
                    boolean phonePresent = endUserList.stream().anyMatch(user -> user.getPhone().equals(endUser.getPhone()));
                    boolean emailPresent = endUserList.stream().anyMatch(user -> user.getEmail().equals(endUser.getEmail()));
                    newEmail = newEmail == null || emailPresent ? endUser.getEmail() : newEmail;
                    newPhone = newPhone == null || phonePresent ? endUser.getPhone() : newPhone;

                    endUser.setFirstName(endUserNew.getFirstName() == null ? endUser.getFirstName() : endUserNew.getFirstName());
                    endUser.setLastName(endUserNew.getLastName() == null ? endUser.getLastName() : endUserNew.getLastName());
                    endUser.setEmail(newEmail);
                    endUser.setPhone(newPhone);
                    endUserDao.update(endUser);
                    response.status(200);
                } else {
                    logger.info("user not found");
                    response.status(400);
                }
            } else {
                logger.info("userId null");
                response.status(400);
            }
        } catch (Exception e) {
        e.printStackTrace();
        response.status(500);
    }
        return response.status();
    };

    public static Route deleteUser = (Request request, Response response) -> {
        try {
            Integer id = getParamUserId(request);
            EndUser endUser = endUserDao.fetchOneById(id);
            if (endUser != null){
                List<Account> accountList = accountDao.fetchByEndUserId(id);
                if (accountList == null || accountList.isEmpty()){
                    endUserDao.delete(endUser);
                    response.status(200);
                } else {
                    logger.info("endUser still have accounts!");
                    response.status(400);
                }
            } else {
                logger.info("endUser not found!");
                response.status(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
        }
        return response.status();
    };
}
