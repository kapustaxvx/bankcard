package com.moskalenko.application.service;

import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.BankCardStatus;
import com.moskalenko.application.dao.BankCardDAO;
import com.moskalenko.application.service.beans.Account;
import com.moskalenko.application.service.beans.BankCardWithAccount;
import com.moskalenko.application.service.beans.NumberHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

@Service
public class BankCardService {

    public static final Logger log = LoggerFactory.getLogger(BankCardService.class);
    private final BankCardDAO bankCardDAO;
    private final InvoiceService invoiceService;
    private final AccountManager accountManager;

    public BankCardService(BankCardDAO bankCardDAO, InvoiceService invoiceService,
                            AccountManager accountManager) {
        this.bankCardDAO = bankCardDAO;
        this.invoiceService = invoiceService;
        this.accountManager = accountManager;
    }

    public BankCard createBankCard(Long accountId) {
        final Account account = accountManager.getAccountById(accountId).orElse(null);
        if (account == null){
            log.info("[{}] Аккаунта не существет", accountId);
            throw new RuntimeException("Аккаунта не существет");
        }
        final String cardNumber = NumberHelper.createCardNumber();
        return bankCardDAO.createBankCard(accountId, cardNumber);
    }

    public Optional<BankCard> getBankCardById(Long cardId){
        return bankCardDAO.getBankCardById(cardId);
    }

    public Optional<BankCardWithAccount> getBankCardWithAccount(Long cardId){
        return  bankCardDAO.getBankCardWithAccount(cardId);
    }


    public Collection<BankCard> getBankCards(Long customerId) {
        return bankCardDAO.getBankCards(customerId);
    }

    public void increaseInvoice(Long cardId, BigDecimal amount) {
        final BankCardWithAccount bankCard = getBankCardWithAccount(cardId).orElse(null);
        if (bankCard == null) {
            log.info("[{}] Карты не сущетсвует", cardId);
            throw new RuntimeException("Карты не сущетсвует");
        }

        if (bankCard.getStatus() != BankCardStatus.CONFIRMED) {
            log.info("[{}] Статус карты [{}]", bankCard.getId(), bankCard.getStatus());
            throw new RuntimeException("Статус карты не подтвержден");
        }

        final Account account = accountManager.getAccountById(bankCard.getAccountId()).orElse(null);
        if (account == null) {
            log.info("[{}] Аккаунта не существует", bankCard.getAccountId());
            throw new RuntimeException("Аккаунта не существует");
        }

        invoiceService.increaseInvoice(bankCard.getAccountId(), amount);
    }

    public void decreaseInvoice(Long cardId, BigDecimal amount) {
        final BankCardWithAccount bankCard = getBankCardWithAccount(cardId).orElse(null);
        if (bankCard == null) {
            log.info("[{}] Карты не существует", cardId);
            throw new RuntimeException("Карты не существует");
        }

        if (bankCard.getStatus() != BankCardStatus.CONFIRMED) {
            log.info("[{}] Статус карты [{}]", bankCard.getId(), bankCard.getStatus());
            throw new RuntimeException("Статус карты не подтвежден");
        }

        final Account account = accountManager.getAccountById(bankCard.getAccountId()).orElse(null);

        if (account == null) {
            log.info("[{}] Аккаунта не существует", bankCard.getAccountId());
            throw new RuntimeException("Аккаунта не существует");
        }

        invoiceService.decreaseInvoice(bankCard.getAccountId(), amount);
    }

    public BankCard confirmBankCardCreation(BankCard bankCard) {
        return bankCardDAO.confirmBankCardCreation(bankCard);
    }
}
