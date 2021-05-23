package com.moskalenko.api;

import com.moskalenko.api.beans.BankCard;

import java.math.BigDecimal;
import java.util.Collection;

public interface CustomerClient {

    BankCard createBankCard(Long accountId);

    Collection<BankCard> getBankCards(Long customerId);

    void increaseBalance(Long cardId, BigDecimal amount);

    void decreaseBalance(Long cardId, BigDecimal amount);

    BigDecimal getBalance(Long accountId);

}
