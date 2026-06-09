package com.ericgrandt.totaleconomy

import com.ericgrandt.totaleconomy.exception.AccountNotFoundException
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException
import com.ericgrandt.totaleconomy.model.Account
import com.ericgrandt.totaleconomy.model.Currency
import java.math.BigDecimal
import java.util.UUID

interface EconomyProvider {
    // /**
    // * Lists the available currencies.
    // *
    // * @return the list of available [Currency]
    // */
    // fun listCurrencies(): List<Currency>

    // addCurrency(currency: Currency): Currency

    /**
     * Retrieve the default currency
     *
     * @return the default [Currency]
     *
     * @throws CurrencyNotFoundException if the default currency is not found
     */
    fun getDefaultCurrency(): Currency

    /**
     * Creates a new account for a player.
     *
     * @param playerId the unique identifier of the player
     * @param currencyCode the currencyCode to attach to this account
     * @param startingBalance optional starting balance
     *
     * @return the created [Account]
     *
     * @throws AccountNotFoundException if the account is not found after successful creation
     */
    @Throws(AccountNotFoundException::class)
    fun createAccount(
        playerId: UUID,
        currencyCode: String,
        startingBalance: BigDecimal = BigDecimal.ZERO,
    ): Account

    /**
     * Retrieve the player's account for the provided currency.
     *
     * @param playerId the unique identifier of this player
     * @param currencyCode the currencyCode for this account
     *
     * @return the [Account]
     *
     * @throws AccountNotFoundException if no account exists for the given [playerId] and [currencyCode]
     */
    @Throws(AccountNotFoundException::class)
    fun getAccount(
        playerId: UUID,
        currencyCode: String,
    ): Account
}
