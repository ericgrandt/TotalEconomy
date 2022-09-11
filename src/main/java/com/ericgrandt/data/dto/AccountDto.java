package com.ericgrandt.data.dto;

import java.sql.Timestamp;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class AccountDto {
    private final String id;
    private final Timestamp created;

    public AccountDto(String id, Timestamp created) {
        this.id = id;
        this.created = created;
    }

    public String getId() {
        return id;
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

        AccountDto that = (AccountDto) o;

        return new EqualsBuilder()
            .append(id, that.id)
            .append(created, that.created)
            .isEquals();
    }
}
