package com.ericgrandt.totaleconomy.model;

import net.kyori.adventure.text.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TECurrency(
    String code, String name, String pluralName, String symbol, int fractionalDigits, boolean isDefault
) implements Currency {
    @Override
    public Component format(BigDecimal amount) {
        var balance = amount.setScale(fractionalDigits, RoundingMode.DOWN);

        if (symbol == null) {
            var suffix = balance.compareTo(BigDecimal.ONE) == 0 ? name : pluralName;
            return Component.text("Balance: %s %s".formatted(balance.toPlainString(), suffix));
        }

        return Component.text("Balance: %s%s".formatted(symbol, balance.toPlainString()));
    }
}
