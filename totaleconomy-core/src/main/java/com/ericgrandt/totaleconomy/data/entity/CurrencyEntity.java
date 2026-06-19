package com.ericgrandt.totaleconomy.data.entity;

import java.time.Instant;

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
