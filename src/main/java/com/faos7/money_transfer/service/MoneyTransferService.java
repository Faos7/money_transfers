package com.faos7.money_transfer.service;

import com.faos7.money_transfer.db.h2.tables.pojos.MoneyTransaction;
import com.faos7.money_transfer.db.h2.tables.pojos.Account;

import java.math.BigDecimal;

public interface MoneyTransferService {

    MoneyTransaction transferMoney(Integer accountFromId, Integer accountToId, BigDecimal amount, String details);
    MoneyTransaction depositeMoney(Integer accountId, BigDecimal amount, String details);
    MoneyTransaction withdrawMoney(Integer accountId, BigDecimal amount, String details);
}
