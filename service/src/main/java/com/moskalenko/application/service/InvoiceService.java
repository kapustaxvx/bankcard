package com.moskalenko.application.service;

import com.moskalenko.application.dao.DecreaseDAO;
import com.moskalenko.application.dao.IncreaseDAO;
import com.moskalenko.application.service.beans.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class InvoiceService {

    public static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final IncreaseDAO increaseDAO;
    private final DecreaseDAO decreaseDAO;
    private final AccountManager accountManager;

    public InvoiceService(IncreaseDAO increaseDAO,
                          DecreaseDAO decreaseDAO, AccountManager accountManager) {
        this.increaseDAO = increaseDAO;
        this.decreaseDAO = decreaseDAO;
        this.accountManager = accountManager;
    }

    public void increaseInvoice(Long accountId, BigDecimal amount) {
        final Account account = accountManager.getAccountById(accountId).orElse(null);
        if (account == null) {
            log.info("[{}] Аккаунта не существует", accountId);
            throw new RuntimeException("Аккаунта не существует");
        }
        increaseDAO.createInvoice(accountId, amount);
    }

    public void decreaseInvoice(Long accountId, BigDecimal amount) {
        final Account account = accountManager.getAccountById(accountId).orElse(null);
        if (account == null) {
            log.info("[{}] Аккаунта не существует", accountId);
            throw new RuntimeException("Аккаунта не существует");
        }

        final boolean isLowBalance = account.getDebit().subtract(account.getCredit())
                .subtract(amount).compareTo(BigDecimal.ZERO) < 0;

        if (isLowBalance) {
            log.info("Недостаточно средств на счету");
            throw new RuntimeException("Недостаточно средств на счету");
        }
        decreaseDAO.createInvoice(accountId, amount);
    }

    public BigDecimal getBalance(Long accountId) {
        final Account account = accountManager.getAccountById(accountId).orElse(null);
        if (account == null) {
            log.info("[{}] Аккаунта не существует", accountId);
            throw new RuntimeException("Аккаунта не существует");
        }
        return account.getDebit().subtract(account.getCredit());
    }
}
