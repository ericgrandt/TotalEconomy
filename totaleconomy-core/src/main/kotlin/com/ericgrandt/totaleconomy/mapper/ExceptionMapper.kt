package com.ericgrandt.totaleconomy.mapper

import com.ericgrandt.totaleconomy.exception.AccountNotFoundException
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

fun Throwable.getMessage(): Component {
    return when (this) {
        is MissingDefaultCurrencyException -> {
            Component
                .text("Error: Default currency does not exist.")
                .color(NamedTextColor.RED)
        }

        is CurrencyNotFoundException -> {
            Component.text("Provided currency not found.").color(NamedTextColor.YELLOW)
        }

        is AccountNotFoundException -> {
            Component
                .text("You don't have an account for this currency.")
                .color(NamedTextColor.YELLOW)
        }

        else -> {
            Component
                .text("An internal error occurred. Please contact an administrator.")
                .color(NamedTextColor.RED)
        }
    }
}
