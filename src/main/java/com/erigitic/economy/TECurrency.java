package com.erigitic.economy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

public class TECurrency implements Currency {
    private final int id;
    private final Text singular;
    private final Text plural;
    private final Text symbol;
    private final int numFractionDigits;
    private final boolean prefixSymbol;
    private final boolean isDefault;

    public TECurrency(int id, Text singular, Text plural, Text symbol, boolean prefixSymbol, boolean isDefault) {
        this.id = id;
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.numFractionDigits = 0;
        this.prefixSymbol = prefixSymbol;
        this.isDefault = isDefault;
    }

    @Override
    public Text getDisplayName() {
        return singular;
    }

    @Override
    public Text getPluralDisplayName() {
        return plural;
    }

    @Override
    public Text getSymbol() {
        return symbol;
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        if (prefixSymbol) {
            return Text.of(symbol, amount.setScale(numFractionDigits, RoundingMode.HALF_DOWN));
        } else {
            return Text.of(amount.setScale(numFractionDigits, RoundingMode.HALF_DOWN), symbol);
        }
    }

    @Override
    public int getDefaultFractionDigits() {
        return numFractionDigits;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public String getName() {
        return singular.toPlain();
    }

    public boolean isPrefixSymbol() {
        return prefixSymbol;
    }
}
