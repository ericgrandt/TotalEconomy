package com.ericgrandt.totaleconomy.common.domain;

public record Currency(
    int id,
    String nameSingular,
    String namePlural,
    String symbol,
    int numFractionDigits,
    boolean isDefault
) {
}
