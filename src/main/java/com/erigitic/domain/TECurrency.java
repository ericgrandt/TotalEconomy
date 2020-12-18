package com.erigitic.domain;

import com.google.common.base.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

public class TECurrency implements Currency {
    private final int id;
    private final String singular;
    private final String plural;
    private final String symbol;
    private final int numFractionDigits;
    private final boolean isDefault;

    public TECurrency(int id, String singular, String plural, String symbol, boolean isDefault) {
        this.id = id;
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.numFractionDigits = 0;
        this.isDefault = isDefault;
    }

    @Override
    public Text getDisplayName() {
        return Text.of(singular);
    }

    @Override
    public Text getPluralDisplayName() {
        return Text.of(plural);
    }

    @Override
    public Text getSymbol() {
        return Text.of(symbol);
    }

    @Override
    public Text format(BigDecimal amount, int numFractionDigits) {
        return Text.of(amount.setScale(numFractionDigits, RoundingMode.HALF_DOWN), symbol);
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
        return singular;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TECurrency other = (TECurrency) o;
        return id == other.id
            && numFractionDigits == other.numFractionDigits
            && isDefault == other.isDefault
            && Objects.equal(singular, other.singular)
            && Objects.equal(plural, other.plural)
            && Objects.equal(symbol, other.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, singular, plural, symbol, numFractionDigits, isDefault);
    }
}
