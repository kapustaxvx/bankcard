package com.moskalenko.api.beans;

import com.google.common.base.Preconditions;

public class CounterpartyBuilder {

    private final Counterparty counterparty = new Counterparty();

    public static CounterpartyBuilder create() {
        return new CounterpartyBuilder();
    }

    public CounterpartyBuilder withId(Long id) {
        counterparty.id = id;
        return this;
    }

    public CounterpartyBuilder withDescription(String description) {
        counterparty.description = description;
        return this;
    }

    public CounterpartyBuilder withVersion(Long version) {
        counterparty.version = version;
        return this;
    }

    public Counterparty build() {
        Preconditions.checkArgument(counterparty.id != null);
        Preconditions.checkArgument(counterparty.description != null);
        Preconditions.checkArgument(counterparty.version != null);
        return counterparty;
    }
}
