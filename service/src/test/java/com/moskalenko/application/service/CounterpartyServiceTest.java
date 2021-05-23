package com.moskalenko.application.service;

import com.moskalenko.api.beans.Counterparty;
import com.moskalenko.api.beans.CounterpartyBuilder;
import com.moskalenko.api.requests.CounterpartyDescriptionRequest;
import com.moskalenko.application.dao.CounterpartyDAO;
import com.moskalenko.application.service.beans.NumberHelper;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class CounterpartyServiceTest {


    @InjectMocks
    private CounterpartyService counterpartyService;
    @Mock
    private CounterpartyDAO counterpartyDAO;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createCounterparty() {
        Counterparty mockCounterparty = generateCounterparty();

        CounterpartyDescriptionRequest counterpartyDescriptionRequest = new CounterpartyDescriptionRequest();
        counterpartyDescriptionRequest.setDescription("Apple");

        Mockito.when(counterpartyDAO.createCounterparty(any(), anyString()))
                .thenReturn(mockCounterparty);

        Counterparty counterparty = counterpartyService.createCounterparty(counterpartyDescriptionRequest);

        Mockito.verify(counterpartyDAO).createCounterparty(any(), anyString());

        Assertions.assertNotNull(counterparty);
        Assertions.assertEquals(mockCounterparty.getId(), counterparty.getId());
        Assertions.assertEquals(mockCounterparty.getDescription(), counterparty.getDescription());
        Assertions.assertEquals(mockCounterparty.getVersion(), counterparty.getVersion());
    }

    @Test
    void getCounterparties() {
        Collection<Counterparty> mockCounterparties = new ArrayList<>();
        Counterparty counterpartyOne = CounterpartyBuilder.create()
                .withId(1L)
                .withDescription("Apple")
                .withVersion(3L)
                .build();
        Counterparty counterpartyTwo = CounterpartyBuilder.create()
                .withId(2L)
                .withDescription("Google")
                .withVersion(3L)
                .build();
        Counterparty counterpartyThree = CounterpartyBuilder.create()
                .withId(3L)
                .withDescription("Sberbank")
                .withVersion(3L)
                .build();
        mockCounterparties.add(counterpartyOne);
        mockCounterparties.add(counterpartyTwo);
        mockCounterparties.add(counterpartyThree);

        Mockito.when(counterpartyDAO.getCounterparties()).thenReturn(mockCounterparties);

        Collection<Counterparty> counterparties = counterpartyService.getCounterparties();

        Mockito.verify(counterpartyDAO).getCounterparties();

        Assertions.assertNotNull(counterparties);
        Assertions.assertEquals(3, counterparties.size());
    }

    private Counterparty generateCounterparty() {
        return CounterpartyBuilder.create()
                .withId(1L)
                .withDescription("Apple")
                .withVersion(3L)
                .build();
    }
}