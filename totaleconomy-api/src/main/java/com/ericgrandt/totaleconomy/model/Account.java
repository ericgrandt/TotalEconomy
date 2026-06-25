package com.ericgrandt.totaleconomy.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represent's a player's account for a specific currency.
 * <p>
 * Each account holds the balance for a single currency. If multiple currencies exist, a player will have multiple
 * accounts (one per currency).
 * </p>
 */
public interface Account {
    /**
     * Gets the unique identifier for this account.
     *
     * @return the UUID attached to the player's Minecraft account
     */
    UUID playerId();

    /**
     * Gets the currency type for this account.
     *
     * @return the currency code associated with the account (e.g., "USD", "EUR")
     */
    String currencyCode();

    /**
     * Gets the current balance for this account.
     *
     * @return the current account balance
     */
    BigDecimal balance();
}
