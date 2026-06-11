package com.ericgrandt.totaleconomy.economy

import com.ericgrandt.totaleconomy.EconomyProvider
import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.CurrencyData
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException
import com.ericgrandt.totaleconomy.exception.DatabaseException
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException
import com.ericgrandt.totaleconomy.model.Account
import com.ericgrandt.totaleconomy.model.Currency
import com.github.michaelbull.result.mapBoth
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.Logger
import java.math.BigDecimal
import java.util.UUID

class Economy(
    private val logger: Logger,
    private val accountData: AccountData,
    private val currencyData: CurrencyData,
) : EconomyProvider {
    override fun getDefaultCurrency(): Currency {
        return transaction {
            currencyData.getDefaultCurrency().mapBoth(
                success = {
                    it
                },
                failure = {
                    when (it) {
                        is NoSuchElementException -> {
                            logger.error("default currency not found", it)
                            throw MissingDefaultCurrencyException(it)
                        }

                        else -> {
                            logger.error("database exception when getting default currency", it)
                            throw DatabaseException(it)
                        }
                    }
                },
            )
        }
    }

    override fun getCurrency(currencyCode: String): Currency {
        return transaction {
            currencyData.getCurrency(currencyCode).mapBoth(
                success = {
                    it
                },
                failure = {
                    when (it) {
                        is NoSuchElementException -> {
                            logger.error("currency not found", it)
                            throw CurrencyNotFoundException(it)
                        }

                        else -> {
                            logger.error("database exception when getting currency", it)
                            throw DatabaseException(it)
                        }
                    }
                },
            )
        }
    }

    override fun createAccount(
        playerId: UUID,
        currencyCode: String,
        startingBalance: BigDecimal,
    ): Account {
        return transaction {
            accountData.createAccount(playerId, currencyCode, startingBalance).mapBoth(
                success = {
                    it
                },
                failure = {
                    when (it) {
                        is NoSuchElementException -> {
                            logger.error("account not found after creation", it)
                            throw AccountNotFoundException(it)
                        }

                        else -> {
                            logger.error("database exception when creating account", it)
                            throw DatabaseException(it)
                        }
                    }
                },
            )
        }
    }

    override fun getAccount(
        playerId: UUID,
        currencyCode: String,
    ): Account {
        return transaction {
            accountData.getAccount(playerId, currencyCode).mapBoth(
                success = {
                    it
                },
                failure = {
                    when (it) {
                        is NoSuchElementException -> {
                            logger.warn("account not found", it)
                            throw AccountNotFoundException(it)
                        }

                        else -> {
                            logger.error("database exception when getting account", it)
                            throw DatabaseException(it)
                        }
                    }
                },
            )
        }
    }
}
