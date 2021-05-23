package com.moskalenko.api;


import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.Customer;
import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.requests.CustomerCreatingRequest;

public interface SupportClient {
    Customer createCustomer(CustomerCreatingRequest customerRequest);

    AccountView createAccount(Long customerId);

    BankCard confirmBankCardCreation(Long cardId);

    void confirmDecrease(Long decreaseId);

    void confirmIncrease(Long increaseId);
}
