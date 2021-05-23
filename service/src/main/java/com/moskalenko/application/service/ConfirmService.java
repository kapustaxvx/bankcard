package com.moskalenko.application.service;

import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.BankCardBuilder;
import com.moskalenko.api.beans.BankCardStatus;
import com.moskalenko.application.dao.BankCardDAO;
import com.moskalenko.application.dao.DecreaseDAO;
import com.moskalenko.application.dao.IncreaseDAO;
import com.moskalenko.application.service.beans.Account;
import com.moskalenko.application.service.beans.UnconfirmedDecrease;
import com.moskalenko.application.service.beans.UnconfirmedIncrease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConfirmService {

    public static final Logger log = LoggerFactory.getLogger(ConfirmService.class);

    private final IncreaseDAO increaseDAO;
    private final DecreaseDAO decreaseDAO;
    private final AccountManager accountManager;
    private final BankCardService bankCardService;


    public ConfirmService(IncreaseDAO increaseDAO, DecreaseDAO decreaseDAO,
                          AccountManager accountManager, BankCardService bankCardService) {
        this.increaseDAO = increaseDAO;
        this.decreaseDAO = decreaseDAO;
        this.accountManager = accountManager;
        this.bankCardService = bankCardService;
    }


    public void confirmIncrease(Long increaseId) {
        final UnconfirmedIncrease unconfirmedIncrease = increaseDAO.getUnconfirmedIncrease(increaseId).orElse(null);
        if (unconfirmedIncrease == null) {
            log.info("[{}] Нет неподтвержденого перевода", increaseId);
            throw new RuntimeException("Нет неподтвержденого перевода");
        }

        final Account account = accountManager.getAccountById(unconfirmedIncrease.getAccountId()).orElse(null);
        if (account == null) {
            log.info("[{}] Аккаунта не существует", unconfirmedIncrease.getAccountId());
            throw new RuntimeException("Аккаунта не существует");
        }

        final Account accountToConfirm = new Account(account.getId(), account.getAccountNumber(),
                account.getDebit().add(unconfirmedIncrease.getAmount()), account.getCredit(),
                account.getVersion());

        accountManager.confirmIncrease(increaseId, accountToConfirm);
    }

    public void confirmDecrease(Long decreaseId) {
        final UnconfirmedDecrease unconfirmedDecrease = decreaseDAO.getUnconfirmedDecrease(decreaseId).orElse(null);
        if (unconfirmedDecrease == null) {
            log.info("[{}] Нет неподтвержденого списания", decreaseId);
            throw new RuntimeException("Нет неподтвержденого списания");
        }

        final Account account = accountManager.getAccountById(unconfirmedDecrease.getAccountId()).orElse(null);
        if (account == null) {
            log.info("[{}] Аккаунта не существует", unconfirmedDecrease.getAccountId());
            throw new RuntimeException("Аккаунта не существует");
        }

        final boolean isLowBalance = account.getDebit().subtract(account.getCredit())
                .subtract(unconfirmedDecrease.getAmount()).compareTo(BigDecimal.ZERO) < 0;

        if (isLowBalance) {
            log.info("Недостаточно средств на счету");
            throw new RuntimeException("Недостаточно средств на счету");
        }

        final Account updatedAccount = new Account(account.getId(),
                account.getAccountNumber(),
                account.getDebit(),
                account.getCredit().add(unconfirmedDecrease.getAmount()),
                account.getVersion());

        accountManager.confirmDecrease(decreaseId, updatedAccount);
    }

    public BankCard confirmBankCardCreation(Long cardId) {
        final BankCard bankCardFromDB = bankCardService.getBankCardById(cardId).orElse(null);

        if (bankCardFromDB == null) {
            log.info("[{}] Карты не существует", cardId);
            throw new RuntimeException("Карты не существует");
        }

        if (bankCardFromDB.getStatus().equals(BankCardStatus.CONFIRMED)) {
            log.info("[{}] Карта уже подтверждена", bankCardFromDB.getId());
            return bankCardFromDB;
        }

        final BankCard bankCard = BankCardBuilder.create()
                .withId(bankCardFromDB.getId())
                .withMaskedCardNumber(bankCardFromDB.getMaskedCardNumber())
                .withStatus(BankCardStatus.CONFIRMED)
                .withVersion(bankCardFromDB.getVersion())
                .build();
        return bankCardService.confirmBankCardCreation(bankCard);
    }
}

