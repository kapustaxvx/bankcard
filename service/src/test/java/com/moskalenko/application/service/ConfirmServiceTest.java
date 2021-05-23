package com.moskalenko.application.service;

import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.BankCardBuilder;
import com.moskalenko.api.beans.BankCardStatus;
import com.moskalenko.application.dao.DecreaseDAO;
import com.moskalenko.application.dao.IncreaseDAO;
import com.moskalenko.application.service.beans.Account;
import com.moskalenko.application.service.beans.UnconfirmedDecrease;
import com.moskalenko.application.service.beans.UnconfirmedIncrease;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConfirmServiceTest {

    @InjectMocks
    private ConfirmService confirmService;
    @Mock
    private IncreaseDAO increaseDAO;
    @Mock
    private DecreaseDAO decreaseDAO;
    @Mock
    private AccountManager accountManager;
    @Mock
    private BankCardService bankCardService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void confirmIncreaseWithUnconfirmedIncreaseException() {
        assertThatThrownBy(() -> confirmService.confirmIncrease(anyLong()))
                .isInstanceOf(RuntimeException.class);

        verify(increaseDAO).getUnconfirmedIncrease(anyLong());
        verify(accountManager, never()).getAccountById(anyLong());
        verify(accountManager, never()).confirmIncrease(anyLong(), any());
    }

    @Test
    void confirmIncreaseWithAccountException() {
        UnconfirmedIncrease mockUnconfirmedIncrease = generatedUnconfirmedIncrease();

        when(increaseDAO.getUnconfirmedIncrease(1L)).thenReturn(Optional.of(mockUnconfirmedIncrease));

        assertThatThrownBy(() -> confirmService.confirmIncrease(1L))
                .isInstanceOf(RuntimeException.class);

        verify(increaseDAO).getUnconfirmedIncrease(1L);
        verify(accountManager).getAccountById(1L);
        verify(accountManager, never()).confirmIncrease(anyLong(), any());
    }

    @Test
    void confirmIncrease() {
        UnconfirmedIncrease mockUnconfirmedIncrease = generatedUnconfirmedIncrease();
        Account mockAccount = generateAccountWithLowBalance();

        when(increaseDAO.getUnconfirmedIncrease(1L)).thenReturn(Optional.of(mockUnconfirmedIncrease));
        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        UnconfirmedIncrease unconfirmedIncrease = increaseDAO.getUnconfirmedIncrease(1L).orElse(null);
        Account account = accountManager.getAccountById(1L).orElse(null);
        confirmService.confirmIncrease(1L);

        verify(increaseDAO, times(2)).getUnconfirmedIncrease(1L);
        verify(accountManager, times(2)).getAccountById(1L);
        verify(accountManager).confirmIncrease(eq(1L), any());

        Assertions.assertNotNull(unconfirmedIncrease);
        Assertions.assertEquals(mockUnconfirmedIncrease.getIncreaseId(), unconfirmedIncrease.getIncreaseId());
        Assertions.assertEquals(mockUnconfirmedIncrease.getAccountId(), unconfirmedIncrease.getAccountId());
        Assertions.assertEquals(mockUnconfirmedIncrease.getUnconfirmedIncreaseId(), unconfirmedIncrease.getUnconfirmedIncreaseId());
        Assertions.assertEquals(mockUnconfirmedIncrease.getAmount(), unconfirmedIncrease.getAmount());
        Assertions.assertEquals(mockUnconfirmedIncrease.getCreationDate(), unconfirmedIncrease.getCreationDate());

        Assertions.assertNotNull(account);
        Assertions.assertEquals(mockAccount.getId(), account.getId());
        Assertions.assertEquals(mockAccount.getAccountNumber(), account.getAccountNumber());
        Assertions.assertEquals(mockAccount.getDebit(), account.getDebit());
        Assertions.assertEquals(mockAccount.getCredit(), account.getCredit());
        Assertions.assertEquals(mockAccount.getVersion(), account.getVersion());
    }

    @Test
    void confirmDecreaseWithUnconfirmedIncreaseException() {
        assertThatThrownBy(() -> confirmService.confirmDecrease(anyLong()))
                .isInstanceOf(RuntimeException.class);

        verify(decreaseDAO).getUnconfirmedDecrease(anyLong());
        verify(accountManager, never()).getAccountById(anyLong());
        verify(accountManager, never()).confirmDecrease(anyLong(), any());
    }

    @Test
    void confirmDecreaseWithAccountException() {
        UnconfirmedDecrease mockUnconfirmedDecrease = generatedUnconfirmedDecrease();

        when(decreaseDAO.getUnconfirmedDecrease(1L)).thenReturn(Optional.of(mockUnconfirmedDecrease));

        assertThatThrownBy(() -> confirmService.confirmDecrease(1L))
                .isInstanceOf(RuntimeException.class);

        verify(decreaseDAO).getUnconfirmedDecrease(1L);
        verify(accountManager).getAccountById(1L);
        verify(accountManager, never()).confirmIncrease(anyLong(), any());
    }

    @Test
    void confirmDecreaseWithBalanceException() {
        UnconfirmedDecrease mockUnconfirmedDecrease = generatedUnconfirmedDecrease();
        Account mockAccount = generateAccountWithLowBalance();

        when(decreaseDAO.getUnconfirmedDecrease(1L)).thenReturn(Optional.of(mockUnconfirmedDecrease));
        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        UnconfirmedDecrease unconfirmedDecrease = decreaseDAO.getUnconfirmedDecrease(1L).orElse(null);
        Account account = accountManager.getAccountById(1L).orElse(null);

        assertThatThrownBy(() -> confirmService.confirmDecrease(1L))
                .isInstanceOf(RuntimeException.class);

        verify(decreaseDAO, times(2)).getUnconfirmedDecrease(1L);
        verify(accountManager, times(2)).getAccountById(1L);
        verify(accountManager, never()).confirmDecrease(anyLong(), any());

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(unconfirmedDecrease);

        Assertions.assertTrue(account.getDebit()
                .subtract(account.getCredit())
                .subtract(unconfirmedDecrease.getAmount())
                .compareTo(BigDecimal.ZERO) < 0);

        Assertions.assertEquals(account.getDebit().subtract(account.getCredit()
                        .subtract(unconfirmedDecrease.getAmount())),
                mockAccount.getDebit().subtract(mockAccount.getCredit()
                        .subtract(mockUnconfirmedDecrease.getAmount())));
    }

    @Test
    void confirmDecrease() {
        UnconfirmedDecrease mockUnconfirmedDecrease = generatedUnconfirmedDecrease();
        Account mockAccount = generateAccountWithHighBalance();

        when(decreaseDAO.getUnconfirmedDecrease(1L)).thenReturn(Optional.of(mockUnconfirmedDecrease));
        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        UnconfirmedDecrease unconfirmedDecrease = decreaseDAO.getUnconfirmedDecrease(1L).orElse(null);
        Account account = accountManager.getAccountById(1L).orElse(null);
        confirmService.confirmDecrease(1L);

        verify(decreaseDAO, times(2)).getUnconfirmedDecrease(1L);
        verify(accountManager, times(2)).getAccountById(1L);
        verify(accountManager).confirmDecrease(eq(1L), any());

        Assertions.assertNotNull(unconfirmedDecrease);
        Assertions.assertEquals(mockUnconfirmedDecrease.getDecreaseId(), unconfirmedDecrease.getDecreaseId());
        Assertions.assertEquals(mockUnconfirmedDecrease.getAccountId(), unconfirmedDecrease.getAccountId());
        Assertions.assertEquals(mockUnconfirmedDecrease.getUnconfirmedDecreaseId(), unconfirmedDecrease.getUnconfirmedDecreaseId());
        Assertions.assertEquals(mockUnconfirmedDecrease.getAmount(), unconfirmedDecrease.getAmount());
        Assertions.assertEquals(mockUnconfirmedDecrease.getCreationDate(), unconfirmedDecrease.getCreationDate());

        Assertions.assertNotNull(account);
        Assertions.assertEquals(mockAccount.getId(), account.getId());
        Assertions.assertEquals(mockAccount.getAccountNumber(), account.getAccountNumber());
        Assertions.assertEquals(mockAccount.getDebit(), account.getDebit());
        Assertions.assertEquals(mockAccount.getCredit(), account.getCredit());
        Assertions.assertEquals(mockAccount.getVersion(), account.getVersion());
    }


    @Test
    void confirmBankCardCreationWithBankCardException() {
        assertThatThrownBy(() -> confirmService.confirmBankCardCreation(anyLong()))
                .isInstanceOf(RuntimeException.class);

        verify(bankCardService).getBankCardById(anyLong());
        verify(bankCardService, never()).confirmBankCardCreation(any());
    }

    @Test
    void confirmBankCardCreationWithConfirmedStatus() {
        BankCard mockBankCard = generateConfirmedBankCard();

        when(bankCardService.getBankCardById(1L)).thenReturn(Optional.ofNullable(mockBankCard));

        BankCard bankCard = bankCardService.getBankCardById(1L).orElse(null);

        verify(bankCardService).getBankCardById(anyLong());
        verify(bankCardService, never()).confirmBankCardCreation(any());

        Assertions.assertNotNull(bankCard);
        Assertions.assertEquals(BankCardStatus.CONFIRMED, bankCard.getStatus());
    }

    @Test
    void confirmBankCardCreation() {
        BankCard mockBankCard = generateBankCard();
        BankCard mockConfirmedBankCard = generateConfirmedBankCard();

        when(bankCardService.getBankCardById(1L)).thenReturn(Optional.ofNullable(mockBankCard));
        when(bankCardService.confirmBankCardCreation(any())).thenReturn(mockConfirmedBankCard);

        BankCard bankCard = bankCardService.getBankCardById(1L).orElse(null);
        BankCard confirmedBankCard = confirmService.confirmBankCardCreation(1L);

        verify(bankCardService, times(2)).getBankCardById(1L);
        verify(bankCardService).confirmBankCardCreation(any());

        Assertions.assertNotNull(bankCard);
        Assertions.assertNotNull(confirmedBankCard);

        Assertions.assertNotEquals(confirmedBankCard.getStatus(), bankCard.getStatus());

        Assertions.assertEquals(bankCard.getId(), mockBankCard.getId());
        Assertions.assertEquals(bankCard.getMaskedCardNumber(), mockBankCard.getMaskedCardNumber());
        Assertions.assertEquals(bankCard.getVersion(), mockBankCard.getVersion());
        Assertions.assertEquals(bankCard.getStatus(), mockBankCard.getStatus());
    }

    private UnconfirmedIncrease generatedUnconfirmedIncrease() {
        return new UnconfirmedIncrease(1L,
                1L,
                BigDecimal.TEN,
                Instant.ofEpochSecond(1000),
                1L);
    }

    private UnconfirmedDecrease generatedUnconfirmedDecrease() {
        return new UnconfirmedDecrease(1L,
                1L,
                BigDecimal.valueOf(1000),
                Instant.ofEpochSecond(1000),
                1L);
    }

    private Account generateAccountWithLowBalance() {
        return new Account(1L,
                "12345123451234512345",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                1L);
    }

    private Account generateAccountWithHighBalance() {
        return new Account(1L,
                "12345123451234512345",
                BigDecimal.valueOf(1000000),
                BigDecimal.ZERO,
                1L);
    }

    private BankCard generateBankCard() {
        return BankCardBuilder.create()
                .withId(1L)
                .withMaskedCardNumber("1234123412341234")
                .withStatus(BankCardStatus.NEW)
                .withVersion(1L)
                .build();
    }

    private BankCard generateConfirmedBankCard() {
        return BankCardBuilder.create()
                .withId(1L)
                .withMaskedCardNumber("1234123412341234")
                .withStatus(BankCardStatus.CONFIRMED)
                .withVersion(1L)
                .build();
    }
}