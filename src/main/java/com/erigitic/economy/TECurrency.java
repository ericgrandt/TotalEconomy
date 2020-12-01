package com.erigitic.economy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

public class TECurrency implements Currency {
    private Text singular;
    private Text plural;
    private Text symbol;
    private int numFractionDigits;
    private boolean isDefault;

    public TECurrency(Text singular, Text plural, Text symbol, boolean isDefault) {
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.numFractionDigits = 0;
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
        return Text.of(symbol, amount.setScale(numFractionDigits, RoundingMode.HALF_DOWN));
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
    public CatalogKey getKey() {
        return CatalogKey.of("totaleconomy", singular.toPlain().toLowerCase());
    }

    @Override
    public String getName() {
        return singular.toPlain();
    }
}
