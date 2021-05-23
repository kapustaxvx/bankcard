package com.moskalenko.application.service;

import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.beans.AccountViewBuilder;
import com.moskalenko.application.dao.AccountDAO;
import com.moskalenko.application.dao.DecreaseDAO;
import com.moskalenko.application.dao.IncreaseDAO;
import com.moskalenko.application.service.beans.Account;
import com.moskalenko.application.service.beans.NumberHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountManager {

    public static final Logger log = LoggerFactory.getLogger(AccountManager.class);
    private final AccountDAO accountDAO;
    private final IncreaseDAO increaseDAO;
    private final DecreaseDAO decreaseDAO;

    public AccountManager(AccountDAO accountDAO, IncreaseDAO increaseDAO,
                          DecreaseDAO decreaseDAO) {
        this.accountDAO = accountDAO;
        this.increaseDAO = increaseDAO;
        this.decreaseDAO = decreaseDAO;
    }

    public AccountView createAccount(Long customerId) {

        final String accountNumber = NumberHelper.createAccountNumber();
        final Account account = accountDAO.createAccount(customerId, accountNumber);

        return AccountViewBuilder.create()
                .withId(account.getId())
                .withAccountNumber(account.getAccountNumber())
                .withBalance(account.getDebit().subtract(account.getCredit()))
                .withVersion(account.getVersion())
                .build();
    }


    @Transactional
    public void confirmIncrease(Long increaseId, Account account) {
        accountDAO.confirmIncreaseBalance(account);
        increaseDAO.removeFromUnconfirmedIncrease(increaseId);
    }

    @Transactional
    public void confirmDecrease(Long decreaseId, Account account) {
        accountDAO.confirmDecreaseBalance(account);
        decreaseDAO.removeFromUnconfirmedDecrease(decreaseId);
    }


    public Optional<Account> getAccountById(Long id) {
        return accountDAO.getAccount(id);
    }
}
