package com.moskalenko.application.service.beans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;


class UnconfirmedIncreaseTest {

    @Test
    void unconfirmedIncreaseTest(){
        UnconfirmedIncrease unconfirmedIncrease = new UnconfirmedIncrease(1L, 1L,
                BigDecimal.ZERO, Instant.ofEpochMilli(1000), 1L);

        Assertions.assertEquals(1L, unconfirmedIncrease.getIncreaseId());
        Assertions.assertEquals(1L, unconfirmedIncrease.getAccountId());
        Assertions.assertEquals(BigDecimal.ZERO, unconfirmedIncrease.getAmount());
        Assertions.assertEquals(Instant.ofEpochMilli(1000), unconfirmedIncrease.getCreationDate());
        Assertions.assertEquals(1L, unconfirmedIncrease.getUnconfirmedIncreaseId());
    }

}