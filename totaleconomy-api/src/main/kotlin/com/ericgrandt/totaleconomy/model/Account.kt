package com.ericgrandt.totaleconomy.model

import java.math.BigDecimal
import java.util.UUID

/**
 * Represent's a player's account for a specific currency.
 *
 * Each account holds the balance for a single currency. If multiple currencies exist, a player will have multiple
 * accounts (one per currency).
 *
 * @property playerId the [UUID] attached to the player's Minecraft account
 * @property currencyCode the [Currency.code] associated with the account
 * @property balance the current account balance
 */
interface Account {
    val playerId: UUID
    val currencyCode: String
    val balance: BigDecimal
}
