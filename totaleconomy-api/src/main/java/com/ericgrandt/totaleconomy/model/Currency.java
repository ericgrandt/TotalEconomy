package com.ericgrandt.totaleconomy.model;

import net.kyori.adventure.text.Component;

import java.math.BigDecimal;

public interface Currency {
    /**
     * Unique identifier for the currency.
     * <p>
     * Examples: USD, EUR, DIAMOND, COINS, etc.
     * </p>
     *
     * @return the unique code
     */
    String getCode();

    /**
     * Singular form of the currency name.
     * <p>
     * Examples: Dollar, Euro, Diamond, Coin
     * </p>
     *
     * @return the singular name of the currency
     */
    String getName();

    /**
     * Plural form of the currency name.
     * <p>
     * Examples: Dollars, Euros, Diamonds, Coins
     * </p>
     *
     * @return the plural name of the currency
     */
    String getPluralName();

    /**
     * Returns the symbol representing the currency.
     * <p>
     * This value can be {@code null} or empty, which allows the {@link #format(BigDecimal)} function to fall back to
     * using the {@link #getName()} or {@link #getPluralName()} instead of the symbol.
     * </p>
     * <p>
     * Examples: "$", "€", "¥", or {@code null}.
     * </p>
     *
     * @return the symbol, or {@code null} if not set
     */
    String getSymbol();

    /**
     * Returns the number of decimal places used for the currency.
     * <p>
     * This defines how the amount is formatted and stored.
     * </p>
     *
     * @return the number of fractional digits
     */
    int getFractionalDigits();

    /**
     * Indicates whether the currency is the default.
     * <p>
     * The default currency is used in situations where no currency is specified.
     * </p>
     *
     * @return {@code true} if this is the default currency, {@code false} otherwise
     */
    boolean isDefault();

    /**
     * Formats an amount into a {@link Component} for displaying.
     * <p>
     * This is used for rendering currency amounts within user interfaces. If the {@link #getSymbol()} is {@code null}
     * or empty, this should fall back to using the {@link #getName()} or {@link #getPluralName()}. The amount should be
     * formatted using the {@link #getFractionalDigits()} defined on the currency.
     * </p>
     *
     * @param amount the amount to format
     * @return a {@link Component} representing the formatted amount
     */
    Component format(BigDecimal amount);
}
