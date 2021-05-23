package com.moskalenko.application.service.beans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;


class UnconfirmedDecreaseTest {

    @Test
    void unconfirmedDecreaseTest(){
        UnconfirmedDecrease unconfirmedDecrease = new UnconfirmedDecrease(1L, 1L,
                 BigDecimal.ZERO, Instant.ofEpochMilli(1000), 1L);

        Assertions.assertEquals(1L, unconfirmedDecrease.getDecreaseId());
        Assertions.assertEquals(1L, unconfirmedDecrease.getAccountId());
        Assertions.assertEquals(BigDecimal.ZERO, unconfirmedDecrease.getAmount());
        Assertions.assertEquals(Instant.ofEpochMilli(1000), unconfirmedDecrease.getCreationDate());
        Assertions.assertEquals(1L, unconfirmedDecrease.getUnconfirmedDecreaseId());

    }
}