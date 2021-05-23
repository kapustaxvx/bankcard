package com.moskalenko.api.requests;

public class CustomerCreatingRequest {
    private String firstName;
    private String secondName;

    public CustomerCreatingRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
}
