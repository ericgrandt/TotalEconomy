package com.ericgrandt.data.dto;

import java.sql.Timestamp;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class VirtualAccountDto {
    private final String id;
    private final String identifier;
    private final Timestamp created;

    public VirtualAccountDto(String id, String identifier, Timestamp created) {
        this.id = id;
        this.identifier = identifier;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Timestamp getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VirtualAccountDto that = (VirtualAccountDto) o;

        return new EqualsBuilder()
            .append(id, that.id)
            .append(identifier, that.identifier)
            .append(created, that.created)
            .isEquals();
    }
}
