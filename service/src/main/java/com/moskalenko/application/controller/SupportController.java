package com.moskalenko.application.controller;

import com.moskalenko.api.SupportClient;
import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.beans.BankCard;
import com.moskalenko.api.beans.Customer;
import com.moskalenko.api.requests.CustomerCreatingRequest;
import com.moskalenko.application.service.ConfirmService;
import com.moskalenko.application.service.CustomerService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/support")
public class SupportController implements SupportClient {

    private final CustomerService customerService;
    private final ConfirmService confirmService;

    public SupportController(CustomerService customerService,
                             ConfirmService confirmService) {
        this.customerService = customerService;
        this.confirmService = confirmService;
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody CustomerCreatingRequest params) {
        return customerService.createCustomer(params);
    }

    @PostMapping("/customers/{customerId}/accounts")
    public AccountView createAccount(@PathVariable Long customerId) {
        return customerService.createAccountForCustomer(customerId);
    }


    @PutMapping("/cards/{cardId}/confirm")
    public BankCard confirmBankCardCreation(@PathVariable Long cardId) {
        return confirmService.confirmBankCardCreation(cardId);
    }

    @PutMapping("/increase/{increaseId}/confirm")
    public void confirmIncrease(@PathVariable Long increaseId) {
        confirmService.confirmIncrease(increaseId);
    }

    @PutMapping(value = "/decrease/{decreaseId}/confirm")
    public void confirmDecrease(@PathVariable Long decreaseId) {
        confirmService.confirmDecrease(decreaseId);
    }


}
