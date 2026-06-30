package com.ericgrandt.totaleconomy.mapper;

import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

public class CommandExceptionMapper {
    private final Logger logger;

    public CommandExceptionMapper(Logger logger) {
        this.logger = logger;
    }

    public Component handleException(Throwable t) {
        return switch (t) {
            case CurrencyNotFoundException ignored -> Component
                .text("The provided currency was not found.")
                .color(NamedTextColor.YELLOW);
            case AccountNotFoundException ignored -> Component
                .text("You don't have an account for this currency.")
                .color(NamedTextColor.YELLOW);
            case MissingDefaultCurrencyException ignored -> {
                logger.error("default currency does not exist", t);
                yield Component
                    .text("An internal error occurred. Please contact an administrator.")
                    .color(NamedTextColor.RED);
            }
            default -> {
                logger.error(t.getMessage(), t);
                yield Component
                    .text("An internal error occurred. Please contact an administrator.")
                    .color(NamedTextColor.RED);
            }
        };
    }
}
