package com.moskalenko.application.service;

import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.beans.Customer;
import com.moskalenko.api.requests.CustomerCreatingRequest;
import com.moskalenko.application.dao.CustomerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    public static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerDAO customerDAO;
    private final AccountManager accountManager;

    public CustomerService(CustomerDAO customerDAO, AccountManager accountManager) {
        this.customerDAO = customerDAO;
        this.accountManager = accountManager;
    }

    public Customer createCustomer(CustomerCreatingRequest params) {
        return customerDAO.createCustomer(params);
    }

    public Optional<Customer> getCustomerById(Long customerId) {
        return customerDAO.getCustomerById(customerId);
    }

    public AccountView createAccountForCustomer(Long customerId) {
        final Customer customer = getCustomerById(customerId).orElse(null);
        if (customer == null) {
            log.info("[{}] Кастомера не сущесвтует", customerId);
            throw new RuntimeException("Кастомера не сущесвтует");
        }
        return accountManager.createAccount(customerId);
    }

}


