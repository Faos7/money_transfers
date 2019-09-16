package com.faos7.money_transfer.util;


public class Path {

    public static class Web {
         public static final String USERS = "/users/";
         public static final String ONE_USER = "/user/:userId/";
         public static final String ONE_USER_BY_PHONE = "/user/:phone/";
         public static final String ONE_USER_BY_EMAIL = "/user/:email/";
         public static final String USER_ACCOUNTS = "/user/accounts/:userId/";

         public static final String USER_CREATE = "/user/create/";
         public static final String USER_UPDATE = "/user/update/:userId/";
         public static final String USER_DELETE = "/user/delete/:userId/";

         public static final String ACCOUNTS = "/accounts/";
         public static final String ONE_ACCOUNT = "/account/:accountId/";
         public static final String ACCOUNT_HOLDER = "/account/holder/:accountId/";
         public static final String ACCOUNT_TRANSACTIONS = "/account/transactions/:accountId/";
         public static final String ACCOUNT_OUTGOING_TRANSACTIONS = "/account/transactions/out/:accountId/";
         public static final String ACCOUNT_INCOMING_TRANSACTIONS = "/account/transactions/in/:accountId/";

         public static final String ACCOUNT_CREATE = "/account/create/";
         public static final String ACCOUNT_UPDATE = "/account/update/:accountId/";
         public static final String ACCOUNT_DELETE = "/account/delete/:accountId/";

         public static final String TRANSACTIONS = "/transactions/";
         public static final String ONE_TRANSACTION = "/transactions/:txId/";

         public static final String DEPOSIT = "/deposit/";
         public static final String WITHDRAW = "/withdraw/";
         public static final String TRANSFER = "/transfer/";

        public static String OK_PATTERN = "[^a-zA-Z0-9:\",{}@_.\\- ]";
    }

}
