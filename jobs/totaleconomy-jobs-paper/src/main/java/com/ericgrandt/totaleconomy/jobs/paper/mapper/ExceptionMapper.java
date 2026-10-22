package com.ericgrandt.totaleconomy.jobs.paper.mapper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

public class ExceptionMapper {
    private final Logger logger;

    public ExceptionMapper(Logger logger) {
        this.logger = logger;
    }

    public Component handleException(Throwable t) {
        return switch (t) {
            default -> {
                logger.error(t.getMessage(), t);
                yield Component
                    .text("An internal error occurred. Please contact an administrator.")
                    .color(NamedTextColor.RED);
            }
        };
    }
}
