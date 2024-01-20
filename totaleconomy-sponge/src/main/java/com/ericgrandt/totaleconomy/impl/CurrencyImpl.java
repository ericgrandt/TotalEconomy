package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.service.economy.Currency;

public class CurrencyImpl implements Currency {
    private final CurrencyDto currencyDto;

    public CurrencyImpl(CurrencyDto currencyDto) {
        this.currencyDto = currencyDto;
    }

    @Override
    public Component displayName() {
        return Component.text(currencyDto.nameSingular());
    }

    @Override
    public Component pluralDisplayName() {
        return Component.text(currencyDto.namePlural());
    }

    @Override
    public Component symbol() {
        return Component.text(currencyDto.symbol());
    }

    @Override
    public Component format(BigDecimal amount, int numFractionDigits) {
        BigDecimal roundedAmount = amount.setScale(numFractionDigits, RoundingMode.DOWN);
        return Component.text(currencyDto.symbol() + roundedAmount);
    }

    @Override
    public int defaultFractionDigits() {
        return currencyDto.numFractionDigits();
    }

    @Override
    public boolean isDefault() {
        return currencyDto.isDefault();
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

        return Objects.equals(currencyDto, currency.currencyDto);
    }
}
