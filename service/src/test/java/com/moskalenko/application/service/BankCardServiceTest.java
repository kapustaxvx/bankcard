package com.moskalenko.application.service;

import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.beans.AccountViewBuilder;
import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.BankCardBuilder;
import com.moskalenko.api.beans.BankCardStatus;
import com.moskalenko.application.dao.BankCardDAO;
import com.moskalenko.application.service.beans.Account;
import com.moskalenko.application.service.beans.BankCardWithAccount;
import com.moskalenko.application.service.beans.NumberHelper;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class BankCardServiceTest {

    @InjectMocks
    private BankCardService bankCardService;
    @Mock
    private BankCardDAO bankCardDAO;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private AccountManager accountManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createBankCard() {
        Account mockAccount = generateAccount();
        BankCard mockBankCard = generateBankCard();

        when(accountManager.getAccountById(anyLong())).thenReturn(Optional.of(mockAccount));
        when(bankCardDAO.createBankCard(anyLong(), anyString())).thenReturn(mockBankCard);

        BankCard bankCard = bankCardService.createBankCard(1L);

        verify(accountManager).getAccountById(anyLong());
        verify(bankCardDAO).createBankCard(eq(1L), anyString());

        Assertions.assertNotNull(bankCard);
        Assertions.assertEquals(mockBankCard.getId(), bankCard.getId());
        Assertions.assertEquals(mockBankCard.getMaskedCardNumber(), bankCard.getMaskedCardNumber());
        Assertions.assertEquals(mockBankCard.getStatus(), bankCard.getStatus());
        Assertions.assertEquals(mockBankCard.getVersion(), bankCard.getVersion());
    }

    @Test
    void createBankCardWithAccountException() {

        assertThatThrownBy(() -> bankCardService.createBankCard(anyLong()))
                .isInstanceOf(RuntimeException.class);

        verify(accountManager).getAccountById(anyLong());
        verify(bankCardDAO, never()).createBankCard(anyLong(), anyString());
    }

    @Test
    void getNotNullBankCardById() {
        BankCard mockBankCard = generateBankCard();

        when(bankCardService.getBankCardById(1L)).thenReturn(Optional.ofNullable(mockBankCard));

        BankCard bankCard = bankCardService.getBankCardById(1L).orElse(null);

        verify(bankCardDAO).getBankCardById(1L);

        Assertions.assertNotNull(bankCard);
        Assertions.assertEquals(mockBankCard.getId(), bankCard.getId());
        Assertions.assertEquals(mockBankCard.getMaskedCardNumber(), bankCard.getMaskedCardNumber());
        Assertions.assertEquals(mockBankCard.getStatus(), bankCard.getStatus());
        Assertions.assertEquals(mockBankCard.getVersion(), bankCard.getVersion());
    }

    @Test
    void getNullBankCardById() {
        Optional<BankCard> mockBankCard = Optional.empty();
        Mockito.when(bankCardService.getBankCardById(1L)).thenReturn(mockBankCard);

        BankCard bankCard = bankCardService.getBankCardById(1L).orElse(null);

        Mockito.verify(bankCardDAO, Mockito.times(1)).getBankCardById(1L);

        Assertions.assertNull(bankCard);
    }

    @Test
    void getNotNullBankCardWithAccountId() {
        BankCardWithAccount mockBankCardWithAccount = generateBankCardWithAccount();
        when(bankCardService.getBankCardWithAccount(1L)).thenReturn(Optional.of(mockBankCardWithAccount));

        BankCardWithAccount bankCardWithAccount = bankCardService.getBankCardWithAccount(1L).orElse(null);

        verify(bankCardDAO).getBankCardWithAccount(1L);

        Assertions.assertNotNull(bankCardWithAccount);
        Assertions.assertEquals(mockBankCardWithAccount.getId(), bankCardWithAccount.getId());
        Assertions.assertEquals(mockBankCardWithAccount.getAccountId(), bankCardWithAccount.getAccountId());
        Assertions.assertEquals(mockBankCardWithAccount.getMaskedCardNumber(), bankCardWithAccount.getMaskedCardNumber());
        Assertions.assertEquals(mockBankCardWithAccount.getStatus(), bankCardWithAccount.getStatus());
        Assertions.assertEquals(mockBankCardWithAccount.getVersion(), bankCardWithAccount.getVersion());
    }

    @Test
    void getNullBankCardWithAccountId() {
        Optional<BankCardWithAccount> mockBankCardWithAccount = Optional.empty();

        when(bankCardService.getBankCardWithAccount(1L)).thenReturn(mockBankCardWithAccount);

        BankCardWithAccount bankCardWithAccount = bankCardService.getBankCardWithAccount(1L).orElse(null);

        verify(bankCardDAO).getBankCardWithAccount(1L);

        Assertions.assertNull(bankCardWithAccount);
    }

    @Test
    void getAllCards() {
        Collection<BankCard> bankCards = new ArrayList<>();
        BankCard bankCardOne = BankCardBuilder.create()
                .withId(1L)
                .withMaskedCardNumber("1234123412341234")
                .withStatus(BankCardStatus.NEW)
                .withVersion(1L)
                .build();
        BankCard bankCardTwo = BankCardBuilder.create()
                .withId(2L)
                .withMaskedCardNumber("1234123412341235")
                .withStatus(BankCardStatus.BLOCKED)
                .withVersion(1L)
                .build();
        BankCard bankCardThree = BankCardBuilder.create()
                .withId(3L)
                .withMaskedCardNumber("1234123412341236")
                .withStatus(BankCardStatus.CONFIRMED)
                .withVersion(1L)
                .build();

        bankCards.add(bankCardOne);
        bankCards.add(bankCardTwo);
        bankCards.add(bankCardThree);

        Mockito.when(bankCardService.getBankCards(1L)).thenReturn(bankCards);

        Collection<BankCard> bankCardCollection = bankCardService.getBankCards(1L);

        Mockito.verify(bankCardDAO).getBankCards(1L);

        Assertions.assertNotNull(bankCardCollection);
        Assertions.assertEquals(3, bankCardCollection.size());
    }

    @Test
    void confirmBankCardCreation() {
        BankCard mockBankCard = generateConfirmedBankCard();

        when(bankCardService.confirmBankCardCreation(generateBankCard())).thenReturn(mockBankCard);

        BankCard bankCard = bankCardService.confirmBankCardCreation(generateBankCard());

        verify(bankCardDAO).confirmBankCardCreation(generateBankCard());

        Assertions.assertEquals(mockBankCard.getStatus(), bankCard.getStatus());
    }


    @Test
    void getBankCardWithAccount() {
        Optional<BankCardWithAccount> bankCardWithAccount = Optional.of(new BankCardWithAccount(1L, 1L,
                "1234123412341234", BankCardStatus.CONFIRMED, 4L));
        Mockito.when(bankCardService.getBankCardWithAccount(1L)).thenReturn(bankCardWithAccount);

        BankCardWithAccount bankCardWithAccountTest = bankCardService.getBankCardWithAccount(1L).orElse(null);

        Assertions.assertNotNull(bankCardWithAccountTest);
        Assertions.assertEquals(1L, bankCardWithAccountTest.getId());
        Assertions.assertEquals(1L, bankCardWithAccountTest.getAccountId());
        Assertions.assertEquals("1234123412341234", bankCardWithAccountTest.getMaskedCardNumber());
        Assertions.assertEquals(BankCardStatus.CONFIRMED, bankCardWithAccountTest.getStatus());
        Assertions.assertEquals(4L, bankCardWithAccountTest.getVersion());

        Mockito.verify(bankCardDAO, Mockito.times(1)).getBankCardWithAccount(1L);
    }


    @Test
    void increaseInvoiceWithBankCardException() {
        assertThatThrownBy(() -> bankCardService.increaseInvoice(1L, BigDecimal.valueOf(200)))
                .isInstanceOf(RuntimeException.class);

        verify(bankCardDAO).getBankCardWithAccount(anyLong());
        verify(accountManager, never()).getAccountById(anyLong());
        verify(invoiceService, never()).increaseInvoice(anyLong(), BigDecimal.valueOf(anyDouble()));
    }

    @Test
    void increaseInvoiceWithConfirmedException() {
        BankCardWithAccount mockBankCardWithAccount = generateBankCardWithAccount();

        when(bankCardDAO.getBankCardWithAccount(1L)).thenReturn(Optional.of(mockBankCardWithAccount));

        assertThatThrownBy(() -> bankCardService.increaseInvoice(1L, BigDecimal.valueOf(200)))
                .isInstanceOf(RuntimeException.class);

        verify(bankCardDAO).getBankCardWithAccount(1L);
        verify(accountManager, never()).getAccountById(anyLong());
        verify(invoiceService, never()).increaseInvoice(anyLong(), BigDecimal.valueOf(anyDouble()));
    }

    @Test
    void increaseInvoiceWithAccountException() {
        BankCardWithAccount mockBankCardWithAccount = generateConfirmBankCardWithAccount();
        when(bankCardDAO.getBankCardWithAccount(1L)).thenReturn(Optional.of(mockBankCardWithAccount));

        BankCardWithAccount bankCardWithAccount = bankCardDAO.getBankCardWithAccount(1L).orElse(null);
        verify(bankCardDAO).getBankCardWithAccount(1L);

        assertThatThrownBy(() -> bankCardService.increaseInvoice(1L, BigDecimal.valueOf(200)))
                .isInstanceOf(RuntimeException.class);

        verify(accountManager).getAccountById(anyLong());
        verify(invoiceService, never()).increaseInvoice(anyLong(), BigDecimal.valueOf(anyDouble()));

        Assertions.assertNotNull(bankCardWithAccount);
        Assertions.assertEquals(mockBankCardWithAccount.getStatus(), bankCardWithAccount.getStatus());
    }

    @Test
    void increaseInvoice() {
        BankCardWithAccount mockBankCardWithAccount = generateConfirmBankCardWithAccount();
        Account mockAccount = generateAccount();

        when(bankCardDAO.getBankCardWithAccount(1L)).thenReturn(Optional.of(mockBankCardWithAccount));
        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        bankCardService.increaseInvoice(1L, BigDecimal.valueOf(200));

        verify(bankCardDAO).getBankCardWithAccount(1L);
        verify(accountManager).getAccountById(1L);
        verify(invoiceService).increaseInvoice(1L, BigDecimal.valueOf(200));
    }

    @Test
    void decreaseInvoiceWithBankCardException() {
        assertThatThrownBy(() -> bankCardService.decreaseInvoice(1L, BigDecimal.valueOf(200)))
                .isInstanceOf(RuntimeException.class);

        verify(bankCardDAO).getBankCardWithAccount(anyLong());
        verify(accountManager, never()).getAccountById(anyLong());
        verify(invoiceService, never()).decreaseInvoice(anyLong(), BigDecimal.valueOf(anyDouble()));
    }

    @Test
    void decreaseInvoiceWithConfirmedException() {
        BankCardWithAccount mockBankCardWithAccount = generateBankCardWithAccount();

        when(bankCardDAO.getBankCardWithAccount(1L)).thenReturn(Optional.of(mockBankCardWithAccount));


        assertThatThrownBy(() -> bankCardService.decreaseInvoice(1L, BigDecimal.valueOf(200)))
                .isInstanceOf(RuntimeException.class);

        verify(bankCardDAO).getBankCardWithAccount(1L);
        verify(accountManager, never()).getAccountById(anyLong());
        verify(invoiceService, never()).decreaseInvoice(anyLong(), BigDecimal.valueOf(anyDouble()));
    }

    @Test
    void decreaseInvoiceWithAccountException() {
        BankCardWithAccount mockBankCardWithAccount = generateConfirmBankCardWithAccount();
        when(bankCardDAO.getBankCardWithAccount(1L)).thenReturn(Optional.of(mockBankCardWithAccount));

        BankCardWithAccount bankCardWithAccount = bankCardDAO.getBankCardWithAccount(1L).orElse(null);

        verify(bankCardDAO).getBankCardWithAccount(1L);

        assertThatThrownBy(() -> bankCardService.decreaseInvoice(1L, BigDecimal.valueOf(200)))
                .isInstanceOf(RuntimeException.class);

        verify(accountManager).getAccountById(anyLong());
        verify(invoiceService, never()).decreaseInvoice(anyLong(), BigDecimal.valueOf(anyDouble()));

        Assertions.assertNotNull(bankCardWithAccount);
        Assertions.assertEquals(mockBankCardWithAccount.getStatus(), bankCardWithAccount.getStatus());
    }

    @Test
    void decreaseInvoice() {
        BankCardWithAccount mockBankCardWithAccount = generateConfirmBankCardWithAccount();
        Account mockAccount = generateAccount();

        when(bankCardDAO.getBankCardWithAccount(1L)).thenReturn(Optional.of(mockBankCardWithAccount));
        when(accountManager.getAccountById(1L)).thenReturn(Optional.of(mockAccount));

        bankCardService.decreaseInvoice(1L, BigDecimal.valueOf(200));

        verify(bankCardDAO).getBankCardWithAccount(1L);
        verify(accountManager).getAccountById(1L);
        verify(invoiceService).decreaseInvoice(1L, BigDecimal.valueOf(200));
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

    private BankCardWithAccount generateBankCardWithAccount() {
        return new BankCardWithAccount(1L,
                1L,
                "1234123412341234",
                BankCardStatus.NEW,
                1L);
    }

    private BankCardWithAccount generateConfirmBankCardWithAccount() {
        return new BankCardWithAccount(1L,
                1L,
                "1234123412341234",
                BankCardStatus.CONFIRMED,
                1L);
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