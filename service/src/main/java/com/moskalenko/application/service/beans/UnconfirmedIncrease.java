package com.moskalenko.application.service.beans;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class UnconfirmedIncrease {
    private final Long increaseId;
    private final Long accountId;
    private final BigDecimal amount;
    private final Instant creationDate;
    private final Long unconfirmedIncreaseId;

    public UnconfirmedIncrease(Long decreaseId, Long accountId, BigDecimal amount, Instant creationDate, Long unconfirmed) {
        this.increaseId = decreaseId;
        this.accountId = accountId;
        this.amount = amount;
        this.creationDate = creationDate;
        this.unconfirmedIncreaseId = unconfirmed;
    }

    public Long getIncreaseId() {
        return increaseId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Long getUnconfirmedIncreaseId() {
        return unconfirmedIncreaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnconfirmedIncrease that = (UnconfirmedIncrease) o;
        return Objects.equals(increaseId, that.increaseId) && Objects.equals(accountId, that.accountId) && Objects.equals(amount, that.amount) && Objects.equals(creationDate, that.creationDate) && Objects.equals(unconfirmedIncreaseId, that.unconfirmedIncreaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(increaseId, accountId, amount, creationDate, unconfirmedIncreaseId);
    }
}
