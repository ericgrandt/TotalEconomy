package com.ericgrandt.data.dto;

import java.sql.Timestamp;

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
}
