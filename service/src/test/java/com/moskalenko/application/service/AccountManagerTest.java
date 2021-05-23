package com.moskalenko.application.service;

import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.beans.AccountViewBuilder;
import com.moskalenko.application.dao.AccountDAO;
import com.moskalenko.application.dao.DecreaseDAO;
import com.moskalenko.application.dao.IncreaseDAO;
import com.moskalenko.application.service.beans.Account;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccountManagerTest {


    @InjectMocks
    private AccountManager accountManager;
    @Mock
    private AccountDAO accountDAO;
    @Mock
    private IncreaseDAO increaseDAO;
    @Mock
    private DecreaseDAO decreaseDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createAccount() {
        Account mockAccount = generateAccount();
        //Настраиваем заглушки
        when(accountDAO.createAccount(anyLong(), anyString())).thenReturn(mockAccount);

        //Вызываем тестируемый метод
        AccountView account = accountManager.createAccount(1L);

        //Проверяем что в результате похода в тест метод были вызваны нужные заглушки
        verify(accountDAO).createAccount(eq(1L), anyString());

        //Проверяем соответсвие полученных данных с ожидаемым
        Assertions.assertNotNull(account);
        Assertions.assertEquals(mockAccount.getId(), account.getId());
        Assertions.assertEquals(mockAccount.getAccountNumber(), account.getAccountNumber());
        Assertions.assertEquals(mockAccount.getVersion(), account.getVersion());
    }


    @Test
    void confirmIncrease() {

        Account account = generateAccount();
        accountManager.confirmIncrease(1L, account);
        Mockito.verify(accountDAO).confirmIncreaseBalance(account);
        Mockito.verify(increaseDAO).removeFromUnconfirmedIncrease(1L);
    }

    @Test
    void confirmIncreaseWithException() {
        Account mockAccount = generateAccount();

        doThrow(RuntimeException.class).when(accountDAO).confirmIncreaseBalance(mockAccount);

        assertThatThrownBy(() -> accountManager.confirmIncrease(1L, mockAccount))
                .isInstanceOf(RuntimeException.class);

        /*try {
            accountManager.confirmIncrease(1L, mockAccount);
        } catch (RuntimeException ignored) {
        }*/
        Mockito.verify(accountDAO).confirmIncreaseBalance(mockAccount);
        Mockito.verify(increaseDAO, never()).removeFromUnconfirmedIncrease(1L);
    }

    @Test
    void confirmDecrease() {
        Account account = generateAccount();
        accountManager.confirmDecrease(3L, account);
        Mockito.verify(accountDAO).confirmDecreaseBalance(account);
        Mockito.verify(decreaseDAO).removeFromUnconfirmedDecrease(3L);
    }

    @Test
    void confirmDecreaseWithException() {
        Account mockAccount = generateAccount();

        doThrow(RuntimeException.class).when(accountDAO).confirmDecreaseBalance(mockAccount);

        assertThatThrownBy(() -> accountManager.confirmDecrease(1L, mockAccount))
                .isInstanceOf(RuntimeException.class);

        Mockito.verify(accountDAO).confirmDecreaseBalance(mockAccount);
        Mockito.verify(decreaseDAO, never()).removeFromUnconfirmedDecrease(1L);
    }

    @Test
    void getNotNullAccountById() {
        Account mockAccount = generateAccount();
        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        Account account = accountManager.getAccountById(1L).orElse(null);

        Mockito.verify(accountDAO).getAccount(1L);

        Assertions.assertNotNull(account);
        Assertions.assertEquals(mockAccount.getId(), account.getId());
        Assertions.assertEquals(mockAccount.getAccountNumber(), account.getAccountNumber());
        Assertions.assertEquals(mockAccount.getDebit(), account.getDebit());
        Assertions.assertEquals(mockAccount.getCredit(), account.getCredit());
        Assertions.assertEquals(mockAccount.getVersion(), account.getVersion());

    }

    @Test
    void getNullAccountById() {
        Optional<Account> mockAccount = Optional.empty();
        when(accountManager.getAccountById(1L)).thenReturn(mockAccount);

        Account account = accountManager.getAccountById(1L).orElse(null);

        Mockito.verify(accountDAO, times(1)).getAccount(1L);

        Assertions.assertNull(account);
    }


    private Account generateAccount() {
        return new Account(1L, "12345123451234512345",
                BigDecimal.ZERO, BigDecimal.ZERO, 0L);
    }

    private AccountView generateAccountView() {
        return AccountViewBuilder.create()
                .withId(1L)
                .withAccountNumber("12345123451234512345")
                .withBalance(BigDecimal.TEN)
                .withVersion(3L)
                .build();
    }


}