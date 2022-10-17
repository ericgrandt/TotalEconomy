package com.ericgrandt.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.service.economy.Currency;

public class CurrencyImpl implements Currency {
    private final int id;
    private final String nameSingular;
    private final String namePlural;
    private final String symbol;
    private final int numFractionDigits;
    private final boolean isDefault;

    public CurrencyImpl(
        int id,
        String nameSingular,
        String namePlural,
        String symbol,
        int numFractionDigits,
        boolean isDefault
    ) {
        this.id = id;
        this.nameSingular = nameSingular;
        this.namePlural = namePlural;
        this.symbol = symbol;
        this.numFractionDigits = numFractionDigits;
        this.isDefault = isDefault;
    }

    public int getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CurrencyImpl currency = (CurrencyImpl) o;

        if (id != currency.id) {
            return false;
        }

        if (numFractionDigits != currency.numFractionDigits) {
            return false;
        }

        if (isDefault != currency.isDefault) {
            return false;
        }

        if (!nameSingular.equals(currency.nameSingular)) {
            return false;
        }

        if (!namePlural.equals(currency.namePlural)) {
            return false;
        }

        return symbol.equals(currency.symbol);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + nameSingular.hashCode();
        result = 31 * result + namePlural.hashCode();
        result = 31 * result + symbol.hashCode();
        result = 31 * result + numFractionDigits;
        result = 31 * result + (isDefault ? 1 : 0);
        return result;
    }
}
