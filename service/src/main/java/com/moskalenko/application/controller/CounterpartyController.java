package com.moskalenko.application.controller;

import com.moskalenko.api.CounterpartyClient;
import com.moskalenko.api.beans.Counterparty;
import com.moskalenko.api.requests.CounterpartyDescriptionRequest;
import com.moskalenko.application.service.CounterpartyService;
import com.moskalenko.application.service.InvoiceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collection;

@RestController
@RequestMapping(value = "/counterparty")
public class CounterpartyController implements CounterpartyClient {

    private final CounterpartyService counterpartyService;
    private final InvoiceService invoiceService;

    public CounterpartyController(CounterpartyService counterpartyService,
                                  InvoiceService invoiceService) {
        this.counterpartyService = counterpartyService;
        this.invoiceService = invoiceService;
    }

    @GetMapping()
    public Collection<Counterparty> getCounterparties() {
        return counterpartyService.getCounterparties();
    }

    @PostMapping()
    public Counterparty createCounterparty(@RequestBody CounterpartyDescriptionRequest description) {
        return counterpartyService.createCounterparty(description);
    }

    @GetMapping("/account/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable Long accountId) {
        return invoiceService.getBalance(accountId);
    }

    @PutMapping("/account/{accountId}/increase/{amount}")
    public void increaseBalance(@PathVariable Long accountId,
                                @PathVariable BigDecimal amount) {
        invoiceService.increaseInvoice(accountId, amount);
    }

    @PutMapping("/account/{accountId}/decrease/{amount}")
    public void decreaseBalance(@PathVariable Long accountId,
                                @PathVariable BigDecimal amount) {
        invoiceService.decreaseInvoice(accountId, amount);
    }

}
