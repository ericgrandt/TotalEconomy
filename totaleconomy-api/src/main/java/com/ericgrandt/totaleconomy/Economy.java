package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import com.ericgrandt.totaleconomy.model.Account;
import com.ericgrandt.totaleconomy.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public interface Economy {
    /**
     * Retrieves the default currency.
     *
     * @return the default {@link Currency}
     * @throws MissingDefaultCurrencyException if the default currency is not found
     */
    Currency getDefaultCurrency();

    /**
     * Retrieves a specific currency by its code.
     *
     * @param currencyCode the unique code of the currency (e.g., "USD", "DIAMOND")
     * @return the {@link Currency} matching the code
     * @throws CurrencyNotFoundException if no currency exists with the given code
     */
    Currency getCurrency(String currencyCode);

    /**
     * Creates a new account for a player with the specified starting balance.
     *
     * @param playerId     the unique identifier of the player
     * @param currencyCode the currency code to associate with this account
     * @param balance      the initial balance for the account
     * @return the newly created {@link Account}
     * @throws AccountNotFoundException if the account fails to be retrieved immediately after creation
     */
    Account createAccount(UUID playerId, String currencyCode, BigDecimal balance);

    /**
     * Creates a new account for a player with a default starting balance of zero.
     * <p>
     * This is an overloaded convenience method equivalent to calling
     * {@code createAccount(playerId, currencyCode, BigDecimal.ZERO)}.
     * </p>
     *
     * @param playerId     the unique identifier of the player
     * @param currencyCode the currency code to associate with this account
     * @return the newly created {@link Account}
     * @throws AccountNotFoundException if the account fails to be retrieved immediately after creation
     */
    default Account createAccount(UUID playerId, String currencyCode) {
        return createAccount(playerId, currencyCode, BigDecimal.ZERO);
    }

    /**
     * Retrieves the player's account for the specified currency.
     *
     * @param playerId     the unique identifier of the player
     * @param currencyCode the currency code for the account
     * @return the existing {@link Account}
     * @throws AccountNotFoundException if no account exists for the given player and currency
     */
    Account getAccount(UUID playerId, String currencyCode);
}
