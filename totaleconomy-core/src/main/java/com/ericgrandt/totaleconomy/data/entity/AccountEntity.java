package com.ericgrandt.totaleconomy.data.entity;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountEntity(
    int id,
    String playerId,
    String currencyCode,
    BigDecimal balance,
    Instant createdAt
) {
}
