package com.ericgrandt.totaleconomy.config;

import java.math.BigDecimal;

public record DefaultCurrencyConfig(
    String code,
    String name,
    String pluralName,
    String symbol,
    int fractionalDigits,
    BigDecimal startingBalance
) {
}
