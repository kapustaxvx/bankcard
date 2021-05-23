package com.moskalenko.application.service;

import com.moskalenko.application.dao.DecreaseDAO;
import com.moskalenko.application.dao.IncreaseDAO;
import com.moskalenko.application.service.beans.Account;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class InvoiceServiceTest {

    @InjectMocks
    InvoiceService invoiceService;
    @Mock
    private IncreaseDAO increaseDAO;
    @Mock
    private DecreaseDAO decreaseDAO;
    @Mock
    private AccountManager accountManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void increaseInvoiceWithAccountException() {
        assertThatThrownBy(() -> invoiceService.increaseInvoice(1L, BigDecimal.valueOf(100)))
                .isInstanceOf(RuntimeException.class);

        verify(accountManager).getAccountById(1L);
        verify(increaseDAO, never()).createInvoice(1L, BigDecimal.valueOf(100));
    }

    @Test
    void increaseInvoice() {
        Account mockAccount = generateAccount();

        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        invoiceService.increaseInvoice(1L, BigDecimal.valueOf(200));

        verify(accountManager).getAccountById(1L);
        verify(increaseDAO).createInvoice(1L, BigDecimal.valueOf(200));
    }

    @Test
    void decreaseInvoiceWithAccountException() {
        assertThatThrownBy(() -> invoiceService.decreaseInvoice(1L, BigDecimal.valueOf(100)))
                .isInstanceOf(RuntimeException.class);

        verify(accountManager).getAccountById(1L);
        verify(decreaseDAO, never()).createInvoice(1L, BigDecimal.valueOf(100));
    }

    @Test
    void decreaseInvoiceWithBalanceException() {
        Account mockAccount = generateAccount();

        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        assertThatThrownBy(() -> invoiceService.decreaseInvoice(1L, BigDecimal.valueOf(1000)))
                .isInstanceOf(RuntimeException.class);

        verify(accountManager).getAccountById(1L);
        verify(decreaseDAO, never()).createInvoice(1L, BigDecimal.valueOf(1000));
    }

    @Test
    void decreaseInvoice() {
        Account mockAccount = generateAccount();

        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        invoiceService.decreaseInvoice(1L, BigDecimal.TEN);

        verify(accountManager).getAccountById(1L);
        verify(decreaseDAO).createInvoice(1L, BigDecimal.TEN);
    }

    @Test
    void getBalanceWithAccountException() {
        assertThatThrownBy(() -> invoiceService.getBalance(1L))
                .isInstanceOf(RuntimeException.class);

        verify(accountManager).getAccountById(1L);
    }

    @Test
    void getBalance() {
        Account mockAccount = generateAccount();

        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        BigDecimal balance = invoiceService.getBalance(1L);

        Assertions.assertEquals(mockAccount.getDebit().subtract(mockAccount.getCredit()), balance);
    }

    private Account generateAccount() {
        return new Account(1L, "12345123451234512345",
                BigDecimal.valueOf(100), BigDecimal.ZERO, 0L);
    }
}