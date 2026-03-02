package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.model.BalanceNotFoundInDatabase
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.ericgrandt.totaleconomy.model.DepositIntoBalance
import com.ericgrandt.totaleconomy.model.DomainError
import com.ericgrandt.totaleconomy.model.ErrorMessage
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.TransferBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.mapError
import net.kyori.adventure.text.Component
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger

class CommonEconomy {
    val accountData: AccountData
    val balanceData: BalanceData

    companion object {
        val logger: Logger = Logger.getLogger(CommonEconomy::class.java.name)
    }

    constructor(accountData: AccountData, balanceData: BalanceData) {
        this.accountData = accountData
        this.balanceData = balanceData
    }

    fun currencyNamePlural(): String {
        return "Diamonds"
    }

    fun currencyNameSingular(): String {
        return "Diamond"
    }

    fun createAccount(uuid: UUID): Result<Int, DomainError> {
        return accountData.createAccount(uuid).mapError {
            logger.log(Level.SEVERE, "error creating account", it)
            DatabaseError
        }
    }

    fun hasAccount(uuid: UUID): Result<Boolean, DomainError> {
        return accountData.getAccount(uuid).mapBoth(
            success = {
                Ok(it != null)
            },
            failure = {
                logger.log(Level.SEVERE, "error getting account", it)
                Err(DatabaseError)
            }
        )
    }

    fun getBalance(uuid: UUID): Result<Double, DomainError> {
        return balanceData.getBalance(uuid).mapBoth(
            success = {
                Ok(it?.balance ?: 0.00)
            },
            failure = {
                logger.log(Level.SEVERE, "error getting balance", it)
                Err(DatabaseError)
            }
        )
    }

    fun setBalance(input: SetBalance): Result<Int, DomainError> {
        return balanceData.setBalance(input).mapBoth(
            success = {
                Ok(it)
            },
            failure = {
                logger.log(Level.SEVERE, "error setting balance", it)
                Err(DatabaseError)
            }
        )
    }

    fun withdrawFromBalance(input: WithdrawFromBalance): Result<Balance, DomainError> {
        // TODO: Add a check to make sure a row was actually updated?
        balanceData.withdrawFromBalance(input).getOrElse {
            logger.log(Level.SEVERE, "error withdrawing from balance", it)
            return Err(DatabaseError)
        }

        return balanceData.getBalance(input.accountId).mapBoth(
            success = {
                if (it == null) {
                    return Err(BalanceNotFoundInDatabase)
                }

                return Ok(it)
            },
            failure = {
                logger.log(Level.SEVERE, "error getting balance", it)
                Err(DatabaseError)
            }
        )
    }

    fun depositIntoBalance(input: DepositIntoBalance): Result<Balance, DomainError> {
        // TODO: Add a check to make sure a row was actually updated?
        balanceData.depositIntoBalance(input).getOrElse {
            logger.log(Level.SEVERE, "error depositing into balance", it)
            return Err(DatabaseError)
        }

        return balanceData.getBalance(input.accountId).mapBoth(
            success = {
                if (it == null) {
                    return Err(BalanceNotFoundInDatabase)
                }

                return Ok(it)
            },
            failure = {
                logger.log(Level.SEVERE, "error getting balance", it)
                Err(DatabaseError)
            }
        )
    }

    fun transferBalance(input: TransferBalance): Result<Boolean, ErrorMessage> {
        // TODO: Verify fromBalance has enough for transfer
        return Ok(true)
    }

    fun format(amount: Double): Component {
        var currencyName = currencyNamePlural()
        if (amount == 1.00) {
            currencyName = currencyNameSingular()
        }
        return Component.text("%.2f $currencyName".format(amount))
    }
}