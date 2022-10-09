package com.ericgrandt.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.service.economy.Currency;

public class CurrencyImpl implements Currency {
    private final String nameSingular;
    private final String namePlural;
    private final String symbol;
    private final int numFractionDigits;
    private final boolean isDefault;
    private final BigDecimal defaultBalance;

    public CurrencyImpl(String nameSingular, String namePlural, String symbol, int numFractionDigits, boolean isDefault, BigDecimal defaultBalance) {
        this.nameSingular = nameSingular;
        this.namePlural = namePlural;
        this.symbol = symbol;
        this.numFractionDigits = numFractionDigits;
        this.isDefault = isDefault;
        this.defaultBalance = defaultBalance;
    }

    @Override
    public Component displayName() {
        return Component.text(nameSingular);
    }

    @Override
    public Component pluralDisplayName() {
        return Component.text(namePlural);
    }

    @Override
    public Component symbol() {
        return Component.text(symbol);
    }

    @Override
    public Component format(BigDecimal amount, int numFractionDigits) {
        BigDecimal roundedAmount = amount.setScale(numFractionDigits, RoundingMode.DOWN);
        return Component.text(roundedAmount.toString());
    }

    @Override
    public int defaultFractionDigits() {
        return this.numFractionDigits;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    public BigDecimal defaultBalance() {
        return defaultBalance;
    }
}
