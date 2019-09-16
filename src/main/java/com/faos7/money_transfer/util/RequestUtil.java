package com.faos7.money_transfer.util;

import spark.Request;

import java.math.BigDecimal;

public class RequestUtil {

    public static String getQueryLocale(Request request) {
        return request.queryParams("locale");
    }

    public static Integer getParamUserId(Request request) {
        String val = request.params("userId");
        return val == null ? null : Integer.valueOf(val);
    }

    public static Integer getParamAccountId(Request request) {
        String val = request.params("accountId");
        return val == null ? null : Integer.valueOf(val);
    }

    public static Integer getParamAccountToId(Request request) {
        String val = request.params("accountToId");
        return val == null ? null : Integer.valueOf(val);
    }

    public static Integer getParamAccountFromId(Request request) {
        String val = request.params("accountFromId");
        return val == null ? null : Integer.valueOf(val);
    }

    public static Integer getParamTxId(Request request) {
        String val = request.params("txId");
        return val == null ? null : Integer.valueOf(val);
    }

    public static String getParamPhone(Request request) {
        return request.params("phone");
    }

    public static String getParamEmail(Request request) {
        return request.params("email");
    }

    public static String getParamFirstName(Request request) {
        return request.params("firstName");
    }

    public static String getParamLastName(Request request) {
        return request.params("lastName");
    }

    public static String getParamName(Request request) {
        return request.params("name");
    }

    public static String getParamDetails(Request request) {
        return request.params("details");
    }

    public static BigDecimal getParamAmount(Request request) {
        String val = request.params("amount");
        return val == null ? null : new BigDecimal(val);
    };

    public static boolean clientAcceptsHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    public static boolean clientAcceptsJson(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("application/json");
    }

}
