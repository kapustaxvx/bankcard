package com.moskalenko.application.service.beans;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CreateNumberTest {

    @Test
    void createAccountNumber() {
        String accountNumber = NumberHelper.createAccountNumber();
        Assertions.assertNotNull(accountNumber);
        Assertions.assertEquals(20, accountNumber.length());
    }

    @Test
    void createCardNumber() {
        String bankCardNumber = NumberHelper.createCardNumber();
        Assertions.assertNotNull(bankCardNumber);
        Assertions.assertEquals(16, bankCardNumber.length());
    }
}