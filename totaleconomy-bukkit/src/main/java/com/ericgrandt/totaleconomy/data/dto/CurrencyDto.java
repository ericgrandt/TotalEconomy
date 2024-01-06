package com.ericgrandt.totaleconomy.data.dto;

public record CurrencyDto(
    int id,
    String nameSingular,
    String namePlural,
    String symbol,
    int numFractionDigits,
    boolean isDefault
) {
}
