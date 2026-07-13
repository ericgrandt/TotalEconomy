package com.ericgrandt.totaleconomy.model;

import java.math.BigDecimal;

public record Config(DatabaseConfig database, DefaultCurrencyConfig defaultCurrency) {
    public record DatabaseConfig(String url, String user, String password) {
    }

    public record DefaultCurrencyConfig(
        String code,
        String name,
        String pluralName,
        String symbol,
        int fractionalDigits,
        BigDecimal startingBalance
    ) {
    }
}
