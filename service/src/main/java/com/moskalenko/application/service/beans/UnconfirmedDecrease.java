package com.moskalenko.application.service.beans;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class UnconfirmedDecrease {
    private final Long decreaseId;
    private final Long accountId;
    private final BigDecimal amount;
    private final Instant creationDate;
    private final Long unconfirmedDecreaseId;

    public UnconfirmedDecrease(Long decreaseId, Long accountId, BigDecimal amount, Instant creationDate, Long unconfirmed) {
        this.decreaseId = decreaseId;
        this.accountId = accountId;
        this.amount = amount;
        this.creationDate = creationDate;
        this.unconfirmedDecreaseId = unconfirmed;
    }

    public Long getDecreaseId() {
        return decreaseId;
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

    public Long getUnconfirmedDecreaseId() {
        return unconfirmedDecreaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnconfirmedDecrease that = (UnconfirmedDecrease) o;
        return Objects.equals(decreaseId, that.decreaseId) && Objects.equals(accountId, that.accountId) && Objects.equals(amount, that.amount) && Objects.equals(creationDate, that.creationDate) && Objects.equals(unconfirmedDecreaseId, that.unconfirmedDecreaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(decreaseId, accountId, amount, creationDate, unconfirmedDecreaseId);
    }
}
