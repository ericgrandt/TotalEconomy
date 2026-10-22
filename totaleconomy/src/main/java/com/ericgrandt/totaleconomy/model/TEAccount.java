package com.ericgrandt.totaleconomy.model;

import com.ericgrandt.totaleconomy.api.model.Account;

import java.math.BigDecimal;
import java.util.UUID;

public record TEAccount(
    UUID playerId,
    String currencyCode,
    BigDecimal balance
) implements Account {
}
