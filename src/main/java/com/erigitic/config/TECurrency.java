package com.erigitic.config;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Eric on 1/1/2016.
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
    public String getName() {
        return singular.toPlain();
    }

    @Override
    public String getId() {
        return "totaleconomy:" + singular.toPlain();
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
        return Text.of(symbol, NumberFormat.getInstance(Locale.ENGLISH).format(amount.setScale(numFractionDigits, BigDecimal.ROUND_HALF_UP)));
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
