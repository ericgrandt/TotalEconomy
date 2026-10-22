package com.ericgrandt.totaleconomy.data.entity;

import java.math.BigDecimal;
import java.time.Instant;

public record CurrencyEntity(
    int id,
    String code,
    String name,
    String pluralName,
    String symbol,
    int fractionalDigits,
    BigDecimal startingBalance,
    boolean isDefault,
    Instant createdAt
) {
}
