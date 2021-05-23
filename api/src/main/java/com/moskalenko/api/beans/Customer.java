package com.moskalenko.api.beans;

import java.util.Objects;

public class Customer {

    Long id;
    String firstName;
    String secondName;
    Long version;

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(firstName, customer.firstName) && Objects.equals(secondName, customer.secondName) && Objects.equals(version, customer.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, secondName, version);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", version=" + version +
                '}';
    }
}
