package com.moskalenko.api.beans;

import com.google.common.base.Preconditions;

public class CustomerBuilder {

    private final Customer customer = new Customer();

    public static CustomerBuilder create() {
        return new CustomerBuilder();
    }

    public CustomerBuilder withId(Long id) {
        customer.id = id;
        return this;
    }

    public CustomerBuilder withFirstName(String firstName) {
        customer.firstName = firstName;
        return this;
    }

    public CustomerBuilder withSecondName(String secondName) {
        customer.secondName = secondName;
        return this;
    }

    public CustomerBuilder withVersion(Long version) {
        customer.version = version;
        return this;
    }

    public Customer build() {
        Preconditions.checkArgument(customer.id != null);
        Preconditions.checkArgument(customer.firstName != null);
        Preconditions.checkArgument(customer.secondName != null);
        Preconditions.checkArgument(customer.version != null);
        return customer;
    }
}
