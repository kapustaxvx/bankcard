package com.moskalenko.api;

import com.moskalenko.api.beans.Counterparty;
import com.moskalenko.api.requests.CounterpartyDescriptionRequest;

import java.math.BigDecimal;
import java.util.Collection;

public interface CounterpartyClient {

    Counterparty createCounterparty(CounterpartyDescriptionRequest request);

    BigDecimal getBalance(Long accountId);

    void increaseBalance(Long accountId, BigDecimal amount);

    void decreaseBalance(Long accountId, BigDecimal amount);

    Collection<Counterparty> getCounterparties();

}
