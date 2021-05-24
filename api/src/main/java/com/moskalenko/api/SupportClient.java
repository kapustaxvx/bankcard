package com.moskalenko.api;


import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.Customer;
import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.requests.CustomerCreatingRequest;

public interface SupportClient {
    /**
     * Создание клиента
     *
     * @param customerRequest данные клиента(имя, фамилия)
     * @return клиент
     */
    Customer createCustomer(CustomerCreatingRequest customerRequest);

    /**
     * Создание лицевого счета
     *
     * @param customerId клиент
     * @return лицевой счет
     */
    AccountView createAccount(Long customerId);

    /**
     * Подтверждение создания карты
     *
     * @param cardId банковская карта
     * @return подтвержденная банковская карта
     */
    BankCard confirmBankCardCreation(Long cardId);

    /**
     * Подтверждение списания средств
     *
     * @param decreaseId идентификатор списания
     */
    void confirmDecrease(Long decreaseId);

    /**
     * Подтверждение пополнения баланса
     *
     * @param increaseId идентификатор пополнения
     */
    void confirmIncrease(Long increaseId);
}
