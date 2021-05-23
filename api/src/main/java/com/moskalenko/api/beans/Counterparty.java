package com.moskalenko.api.beans;

import java.util.Objects;

public class Counterparty {

    Long id;
    String description;
    Long version;

    public Counterparty() {
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Counterparty that = (Counterparty) o;
        return Objects.equals(id, that.id) && Objects.equals(description, that.description) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, version);
    }

    @Override
    public String toString() {
        return "Counterparty{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", version=" + version +
                '}';
    }
}
