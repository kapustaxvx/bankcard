package com.moskalenko.application.service.beans;

import com.moskalenko.api.beans.BankCardStatus;

import java.util.Objects;

public class BankCardWithAccount {
    private final Long id;
    private final Long account_id;


    private final String maskedCardNumber;
    private final BankCardStatus status;

    public BankCardWithAccount(Long id, Long account_id, String maskedCardNumber, BankCardStatus status, Long version) {
        this.id = id;
        this.account_id = account_id;
        this.maskedCardNumber = maskedCardNumber;
        this.status = status;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return account_id;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public BankCardStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }

    private final Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankCardWithAccount that = (BankCardWithAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(account_id, that.account_id) && Objects.equals(maskedCardNumber, that.maskedCardNumber) && status == that.status && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account_id, maskedCardNumber, status, version);
    }
}
