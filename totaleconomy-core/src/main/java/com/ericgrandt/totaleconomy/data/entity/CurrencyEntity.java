package com.ericgrandt.totaleconomy.data.entity;

import java.time.Instant;

// TODO: Add a default balance field
public record CurrencyEntity(
    int id,
    String code,
    String name,
    String pluralName,
    String symbol,
    int fractionalDigits,
    boolean isDefault,
    Instant createdAt
) {
}
