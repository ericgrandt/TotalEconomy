package com.ericgrandt.totaleconomy.mapper;

import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ExceptionMapper {
    public static Component getMessage(Throwable t) {
        return switch (t) {
            case MissingDefaultCurrencyException ignored -> Component
                .text("Error: Default currency does not exist.")
                .color(NamedTextColor.RED);
            case CurrencyNotFoundException ignored -> Component
                .text("Provided currency not found.")
                .color(NamedTextColor.YELLOW);
            case AccountNotFoundException ignored -> Component
                .text("You don't have an account for this currency.")
                .color(NamedTextColor.YELLOW);
            default -> Component
                .text("An internal error occurred. Please contact an administrator.")
                .color(NamedTextColor.RED);
        };
    }
}
