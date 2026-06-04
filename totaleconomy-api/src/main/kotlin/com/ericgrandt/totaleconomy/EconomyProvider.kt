package com.ericgrandt.totaleconomy

import com.ericgrandt.totaleconomy.model.Account
import java.math.BigDecimal
import java.util.UUID

interface EconomyProvider {
    /**
     * Creates a new account for a player.
     *
     * @param accountId the unique identifier of the player
     * @param currencyCode the currencyCode to attach to this account
     * @param startingBalance optional starting balance
     *
     * @return the created [Account]
     *
     * @throws AccountCreationException
     */
    fun createAccount(
        accountId: UUID,
        currencyCode: String,
        startingBalance: BigDecimal = BigDecimal.ZERO,
    ): Account
}
