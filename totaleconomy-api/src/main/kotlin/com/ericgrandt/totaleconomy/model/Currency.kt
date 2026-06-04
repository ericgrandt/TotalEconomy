package com.ericgrandt.totaleconomy.model

import net.kyori.adventure.text.Component
import java.math.BigDecimal

interface Currency {
    /**
     * Unique identifier for this currency.
     *
     * Examples: USD, EUR, DIAMOND, COINS, etc.
     */
    val code: String

    /**
     * Singular form of the currency name.
     *
     * Examples: Dollar, Euro, Diamond, Coin
     */
    val name: String

    /**
     * Plural form of the currency name.
     *
     * Examples: Dollars, Euros, Diamonds, Coins
     */
    val pluralName: String

    /**
     * Symbol representing the currency.
     *
     * This value can be null or empty, which allows the [format] function to fall back to using the [name] or
     * [pluralName] instead of the symbol.
     *
     * Examples: $, €, ¥, or null
     */
    val symbol: String?

    /**
     * Number of decimal places used for the currency.
     *
     * This defines how the amount is formatted and stored.
     */
    val fractionalDigits: Int

    /**
     * Indicates whether the currency is the default.
     *
     * The default currency is used in situations where no currency is specified.
     */
    val isDefault: Boolean

    /**
     * Formats an amount into a [Component] for displaying.
     *
     * This is used for rendering currency amounts within user interfaces. If the [symbol] is null or empty, this
     * should fall back to using the [name] or [pluralName]. The amount should be formatted using the [fractionalDigits]
     * defined on the currency.
     *
     * @param amount the amount to format
     * @return a [Component] representing the formatted amount
     */
    fun format(amount: BigDecimal): Component
}
