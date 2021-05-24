package com.moskalenko.api;

import com.moskalenko.api.beans.BankCard;

import java.math.BigDecimal;
import java.util.Collection;

public interface CustomerClient {

    /**
     * Создание банковской карты для клиента
     *
     * @param accountId аккаунт лицевого счета
     * @return банковская карта
     */
    BankCard createBankCard(Long accountId);

    /**
     * Список всех банковских карт клиента
     *
     * @param customerId клиент
     * @return список карт
     */
    Collection<BankCard> getBankCards(Long customerId);

    /**
     * Запрос на пополнение баланса
     *
     * @param cardId карта клиента
     * @param amount сумма пополнения
     */
    void increaseBalance(Long cardId, BigDecimal amount);

    /**
     * Запрос на списание с баланса
     *
     * @param cardId карта клиента
     * @param amount сумма списания
     */
    void decreaseBalance(Long cardId, BigDecimal amount);

    /**
     * Запрос на получение баланса
     *
     * @param accountId аккаунт лицевого счета
     * @return баланс
     */
    BigDecimal getBalance(Long accountId);

}
