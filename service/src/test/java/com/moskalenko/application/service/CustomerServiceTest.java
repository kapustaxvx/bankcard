package com.moskalenko.application.service;

import com.moskalenko.api.beans.AccountView;
import com.moskalenko.api.beans.AccountViewBuilder;
import com.moskalenko.api.beans.Customer;
import com.moskalenko.api.beans.CustomerBuilder;
import com.moskalenko.api.requests.CustomerCreatingRequest;
import com.moskalenko.application.dao.CustomerDAO;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerDAO customerDAO;
    @Mock
    private AccountManager accountManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer() {
        Customer mockCustomer = generateCustomer();

        CustomerCreatingRequest customerRequest = new CustomerCreatingRequest();
        customerRequest.setFirstName("Ivan");
        customerRequest.setSecondName("Ivanov");

        Mockito.when(customerDAO.createCustomer(customerRequest)).thenReturn(mockCustomer);

        Customer customer = customerService.createCustomer(customerRequest);

        Mockito.verify(customerDAO).createCustomer(customerRequest);

        Assertions.assertNotNull(customer);
        Assertions.assertEquals(mockCustomer.getId(), customer.getId());
        Assertions.assertEquals(mockCustomer.getFirstName(), customer.getFirstName());
        Assertions.assertEquals(mockCustomer.getSecondName(), customer.getSecondName());
        Assertions.assertEquals(mockCustomer.getVersion(), customer.getVersion());
    }

    @Test
    void getNotNullCustomerById() {
        Customer mockCustomer = generateCustomer();

        Mockito.when(customerDAO.getCustomerById(1L)).thenReturn(Optional.ofNullable(mockCustomer));

        Customer customer = customerService.getCustomerById(1L).orElse(null);

        Mockito.verify(customerDAO).getCustomerById(1L);

        Assertions.assertNotNull(customer);
        Assertions.assertEquals(mockCustomer.getId(), customer.getId());
        Assertions.assertEquals(mockCustomer.getFirstName(), customer.getFirstName());
        Assertions.assertEquals(mockCustomer.getSecondName(), customer.getSecondName());
        Assertions.assertEquals(mockCustomer.getVersion(), customer.getVersion());
    }

    @Test
    void getNullCustomerById() {
        Optional<Customer> mockCustomer = Optional.empty();

        Mockito.when(customerDAO.getCustomerById(1L)).thenReturn(mockCustomer);

        Customer customer = customerService.getCustomerById(1L).orElse(null);

        Mockito.verify(customerDAO).getCustomerById(1L);

        Assertions.assertNull(customer);
    }

    @Test
    void createAccountForCustomerWithCustomerException() {

        assertThatThrownBy(() -> customerService.createAccountForCustomer(anyLong()))
                .isInstanceOf(RuntimeException.class);

        Mockito.verify(customerDAO).getCustomerById(anyLong());
        Mockito.verify(accountManager, Mockito.never()).createAccount(anyLong());
    }

    @Test
    void createAccountForCustomer(){
        Customer mockCustomer = generateCustomer();
        AccountView mockAccountView = generateAccountView();
        Mockito.when(customerDAO.getCustomerById(1L)).thenReturn(Optional.ofNullable(mockCustomer));
        Mockito.when(accountManager.createAccount(1L)).thenReturn(mockAccountView);


        AccountView accountView = customerService.createAccountForCustomer(1L);

        Mockito.verify(customerDAO).getCustomerById(1L);
        Mockito.verify(accountManager).createAccount(1L);

        Assertions.assertNotNull(accountView);
        Assertions.assertEquals(mockAccountView.getId(), accountView.getId());
        Assertions.assertEquals(mockAccountView.getAccountNumber(), accountView.getAccountNumber());
        Assertions.assertEquals(mockAccountView.getBalance(), accountView.getBalance());
        Assertions.assertEquals(mockAccountView.getVersion(), accountView.getVersion());
    }

    private Customer generateCustomer() {
        return CustomerBuilder.create()
                .withId(1L)
                .withFirstName("Ivan")
                .withSecondName("Ivanov")
                .withVersion(2L)
                .build();
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