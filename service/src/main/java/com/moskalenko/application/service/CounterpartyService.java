package com.moskalenko.application.service;

import com.moskalenko.api.beans.Counterparty;
import com.moskalenko.api.requests.CounterpartyDescriptionRequest;
import com.moskalenko.application.dao.CounterpartyDAO;
import com.moskalenko.application.service.beans.NumberHelper;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CounterpartyService {

    private final CounterpartyDAO counterpartyDAO;

    public CounterpartyService(CounterpartyDAO counterpartyDAO) {
        this.counterpartyDAO = counterpartyDAO;
    }

    public Counterparty createCounterparty(CounterpartyDescriptionRequest description) {
        final String accountNumber = NumberHelper.createAccountNumber();
        return counterpartyDAO.createCounterparty(description, accountNumber);
    }

    public Collection<Counterparty> getCounterparties() {
        return counterpartyDAO.getCounterparties();
    }


}
