package com.faos7.money_transfer.service.impl;

import com.faos7.money_transfer.db.h2.tables.pojos.Account;
import com.faos7.money_transfer.db.h2.tables.pojos.MoneyTransaction;
import com.faos7.money_transfer.service.MoneyTransferService;

import static com.faos7.money_transfer.ApplicationMain.accountDao;
import static com.faos7.money_transfer.ApplicationMain.moneyTransactionDao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MoneyTransferServiceImpl implements MoneyTransferService {


    public MoneyTransaction transferMoney(Integer accountFromId, Integer accountToId, BigDecimal amount, String details) {
        if (accountFromId != null && accountToId != null && amount != null &&amount.compareTo(BigDecimal.ZERO) >= 0) {
            Account accountFrom = accountDao.findById(accountFromId);
            Account accountTo = accountDao.findById(accountToId);
            LocalDateTime createdAt = LocalDateTime.now();
            if (accountFrom.getBalance().compareTo(amount) >= 0) {
                MoneyTransaction transaction = new MoneyTransaction();
                transaction.setFromAccountId(accountFromId);
                transaction.setToAccountId(accountToId);
                transaction.setAmount(amount);
                transaction.setCreatedAt(createdAt);
                transaction.setDetails(details);

                accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
                accountTo.setBalance(accountTo.getBalance().add(amount));

                moneyTransactionDao.insert(transaction);
            }
            List<MoneyTransaction> txList = moneyTransactionDao.fetchByCreatedAt(createdAt);
            if (txList != null && !txList.isEmpty()) {
                return txList.stream().filter(tx -> tx.getFromAccountId().equals(accountFromId))
                        .filter(tx -> tx.getToAccountId().equals(accountToId))
                        .filter(tx -> tx.getAmount().equals(amount))
                        .findFirst().orElse(null);
            }
        }
        return null;
    }

    public MoneyTransaction depositeMoney(Integer accountId, BigDecimal amount, String details){
        if (accountId != null) {

            LocalDateTime createdAt = LocalDateTime.now();
            if (amount != null && amount.compareTo(BigDecimal.ZERO) >= 0) {
                Account accountTo = accountDao.findById(accountId);

                MoneyTransaction transaction = new MoneyTransaction();
                transaction.setToAccountId(accountId);
                transaction.setAmount(amount);
                transaction.setCreatedAt(createdAt);
                transaction.setDetails(details);

                accountTo.setBalance(accountTo.getBalance().add(amount));

                moneyTransactionDao.insert(transaction);
                accountDao.update(accountTo);
            }
            List<MoneyTransaction> txList = moneyTransactionDao.fetchByCreatedAt(createdAt);
            if (txList != null && !txList.isEmpty()) {
                return txList.stream().filter(tx -> tx.getToAccountId().equals(accountId))
                        .filter(tx -> tx.getAmount().equals(amount))
                        .findFirst().orElse(null);
            }
        }
        return null;
    }

    public MoneyTransaction withdrawMoney(Integer accountId, BigDecimal amount, String details){
        if (accountId != null) {
            LocalDateTime createdAt = LocalDateTime.now();
            if (amount != null && amount.compareTo(BigDecimal.ZERO) >= 0 ) {
                Account accountFrom = accountDao.findById(accountId);
                if (accountFrom.getBalance().compareTo(amount) >= 0) {
                    MoneyTransaction transaction = new MoneyTransaction();
                    transaction.setFromAccountId(accountId);
                    transaction.setAmount(amount);
                    transaction.setCreatedAt(createdAt);
                    transaction.setDetails(details);

                    accountFrom.setBalance(accountFrom.getBalance().subtract(amount));

                    moneyTransactionDao.insert(transaction);
                }
            }
            List<MoneyTransaction> txList = moneyTransactionDao.fetchByCreatedAt(createdAt);
            if (txList != null && !txList.isEmpty()) {
                return txList.stream().filter(tx -> tx.getFromAccountId().equals(accountId))
                        .filter(tx -> tx.getAmount().equals(amount))
                        .findFirst().orElse(null);
            }
        }
        return null;
    }
}
