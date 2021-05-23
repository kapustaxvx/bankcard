package com.moskalenko.application.service.beans;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final Long id;
    private final String accountNumber;
    private final BigDecimal debit;
    private final BigDecimal credit;
    private final Long version;

    public Account(Long id, String accountNumber, BigDecimal debit, BigDecimal credit, Long version) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.debit = debit;
        this.credit = credit;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(accountNumber, account.accountNumber) && Objects.equals(debit, account.debit) && Objects.equals(credit, account.credit) && Objects.equals(version, account.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, debit, credit, version);
    }
}
