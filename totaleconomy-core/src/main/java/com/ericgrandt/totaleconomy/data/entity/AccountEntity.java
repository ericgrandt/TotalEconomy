package com.ericgrandt.totaleconomy.data.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountEntity(
    int id,
    UUID playerId,
    String currencyCode,
    BigDecimal balance,
    Instant createdAt
) {
}
