package com.moskalenko.api.beans;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

public class AccountViewBuilder {

    private final AccountView accountView = new AccountView();

    public static AccountViewBuilder create() {
        return new AccountViewBuilder();
    }

    public AccountViewBuilder withId(Long id) {
        accountView.id = id;
        return this;
    }

    public AccountViewBuilder withAccountNumber(String accountNumber) {
        accountView.accountNumber = accountNumber;
        return this;
    }

    public AccountViewBuilder withBalance(BigDecimal balance) {
        accountView.balance = balance;
        return this;
    }

    public AccountViewBuilder withVersion(Long version) {
        accountView.version = version;
        return this;
    }

    public AccountView build() {
        Preconditions.checkArgument(accountView.id != null);
        Preconditions.checkArgument(accountView.accountNumber != null);
        Preconditions.checkArgument(accountView.balance != null);
        Preconditions.checkArgument(accountView.version != null);
        return accountView;
    }
}
