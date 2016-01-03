package com.erigitic.config;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 1/1/2016.
 */
public class TECurrency implements Currency {

    private Text singular;
    private Text plural;
    private Text symbol;
    private int numFractionDigits;
    private boolean defaultCurrency;

    public TECurrency(Text singular, Text plural, Text symbol, int numFractionDigits, boolean defaultCurrency) {
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.numFractionDigits = numFractionDigits;
        this.defaultCurrency = defaultCurrency;
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
        return Text.of(amount.setScale(numFractionDigits, BigDecimal.ROUND_HALF_UP));
    }

    @Override
    public int getDefaultFractionDigits() {
        return numFractionDigits;
    }


    @Override
    public boolean isDefault() {
        return defaultCurrency;
    }
}
