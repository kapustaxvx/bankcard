package com.moskalenko.api.beans;

import com.google.common.base.Preconditions;

public class BankCardBuilder {
    private final BankCard bankCard = new BankCard();

    public static BankCardBuilder create() {
        return new BankCardBuilder();
    }

    public BankCardBuilder withId(Long id) {
        bankCard.id = id;
        return this;
    }

    public BankCardBuilder withMaskedCardNumber(String maskedCardNumber) {
        bankCard.maskedCardNumber = maskedCardNumber;
        return this;
    }

    public BankCardBuilder withStatus(BankCardStatus status) {
        bankCard.status = status;
        return this;
    }

    public BankCardBuilder withVersion(Long version) {
        bankCard.version = version;
        return this;
    }

    public BankCard build() {
        Preconditions.checkArgument(bankCard.id != null);
        Preconditions.checkArgument(bankCard.maskedCardNumber != null);
        Preconditions.checkArgument(bankCard.status != null);
        Preconditions.checkArgument(bankCard.version != null);
        return bankCard;
    }
}
