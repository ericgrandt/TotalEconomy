package com.ericgrandt.data.dto;

import java.sql.Timestamp;

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
}
