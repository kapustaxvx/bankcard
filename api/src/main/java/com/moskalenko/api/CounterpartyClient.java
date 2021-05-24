package com.moskalenko.api;

import com.moskalenko.api.beans.Counterparty;
import com.moskalenko.api.requests.CounterpartyDescriptionRequest;

import java.math.BigDecimal;
import java.util.Collection;

public interface CounterpartyClient {

    /**
     * Создание контрагента
     * @param request информация о контрагенте(название компании)
     * @return контрагент
     */
    Counterparty createCounterparty(CounterpartyDescriptionRequest request);

    /**
     * Получение баланса
     *
     * @param accountId аккаунт лицевого счета
     * @return баланс
     */
    BigDecimal getBalance(Long accountId);

    /**
     * Запрос на пополнение баланса
     *
     * @param accountId аккаунт лицевого счета
     * @param amount сумма пополнения
     */
    void increaseBalance(Long accountId, BigDecimal amount);

    /**
     * Запрос на списание баланса
     *
     * @param accountId аккаунт лицевого счета
     * @param amount сумма списания
     */
    void decreaseBalance(Long accountId, BigDecimal amount);

    /**
     * Список всех контрагентов
     *
     * @return список контрагентов
     */
    Collection<Counterparty> getCounterparties();

}
