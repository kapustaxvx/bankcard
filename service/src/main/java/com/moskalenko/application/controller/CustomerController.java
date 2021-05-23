package com.moskalenko.application.controller;

import com.moskalenko.api.CustomerClient;
import com.moskalenko.api.beans.BankCard;
import com.moskalenko.application.service.BankCardService;
import com.moskalenko.application.service.InvoiceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collection;

@RestController
@RequestMapping(value = "/customer")
public class CustomerController implements CustomerClient {

    private final BankCardService bankCardService;
    private final InvoiceService invoiceService;


    public CustomerController(BankCardService bankCardService, InvoiceService invoiceService) {
        this.bankCardService = bankCardService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/account/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable Long accountId) {
        return invoiceService.getBalance(accountId);
    }

    @PostMapping("/account/{accountId}/cards")
    public BankCard createBankCard(@PathVariable Long accountId) {
        return bankCardService.createBankCard(accountId);
    }

    @GetMapping("/{customerId}/cards")
    public Collection<BankCard> getBankCards(@PathVariable Long customerId) {
        return bankCardService.getBankCards(customerId);
    }

    @PutMapping("/card/{cardId}/increase/{amount}")
    public void increaseBalance(@PathVariable Long cardId,
                                @PathVariable BigDecimal amount) {
        bankCardService.increaseInvoice(cardId, amount);
    }

    @PutMapping("/card/{cardId}/decrease/{amount}")
    public void decreaseBalance(@PathVariable Long cardId,
                                @PathVariable BigDecimal amount) {
        bankCardService.decreaseInvoice(cardId, amount);
    }
}
