package com.moskalenko.api.beans;

import java.util.Objects;

public class BankCard {

    Long id;
    String maskedCardNumber;
    BankCardStatus status;
    Long version;

    public BankCard() {
    }

    public Long getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankCard bankCard = (BankCard) o;
        return Objects.equals(id, bankCard.id) && Objects.equals(maskedCardNumber, bankCard.maskedCardNumber) && status == bankCard.status && Objects.equals(version, bankCard.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maskedCardNumber, status, version);
    }

    @Override
    public String toString() {
        return "BankCard{" +
                "id=" + id +
                ", maskedCardNumber='" + maskedCardNumber + '\'' +
                ", status=" + status +
                ", version=" + version +
                '}';
    }
}
