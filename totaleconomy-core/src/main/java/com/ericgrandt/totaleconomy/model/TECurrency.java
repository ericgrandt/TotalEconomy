package com.ericgrandt.totaleconomy.model;

import net.kyori.adventure.text.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// TODO: Test
public record TECurrency(
    String code, String name, String pluralName, String symbol, int fractionalDigits, boolean isDefault
) implements Currency {
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPluralName() {
        return pluralName;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getFractionalDigits() {
        return fractionalDigits;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Component format(BigDecimal amount) {
        var balance = amount.setScale(fractionalDigits, RoundingMode.DOWN);

        if (symbol == null) {
            var suffix = balance.compareTo(BigDecimal.ONE) == 0 ? name : pluralName;
            return Component.text("%s %s".formatted(balance.toPlainString(), suffix));
        }

        return Component.text("%s%s".formatted(symbol, balance.toPlainString()));
    }
}
