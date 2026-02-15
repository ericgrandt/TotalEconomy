package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.ericgrandt.totaleconomy.model.DatabaseErrorN
import com.ericgrandt.totaleconomy.model.DepositIntoBalance
import com.ericgrandt.totaleconomy.model.DomainError
import com.ericgrandt.totaleconomy.model.ErrorMessage
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.TransferBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ericgrandt.totaleconomy.result.Err as ErrOld
import com.ericgrandt.totaleconomy.result.Ok as OkOld
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import com.ericgrandt.totaleconomy.result.Result as ResultOld
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

    fun getBalance(uuid: UUID): ResultOld<Double, ErrorMessage> {
        return when (val result = balanceData.getBalance(uuid)) {
            is OkOld -> {
                OkOld(result.value?.balance ?: 0.00)
            }
            is ErrOld -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                ErrOld(DatabaseErrorN)
            }
        }
    }

    // TODO: Return updated balance?
    fun setBalance(input: SetBalance): ResultOld<Int, ErrorMessage> {
        return when (val result = balanceData.setBalance(input)) {
            is OkOld -> {
                OkOld(result.value)
            }
            is ErrOld -> {
                logger.log(Level.SEVERE, "error setting balance", result.error)
                ErrOld(DatabaseErrorN)
            }
        }
    }

    fun withdrawFromBalance(input: WithdrawFromBalance): ResultOld<Balance, ErrorMessage> {
        // TODO: Add a check to make sure a row was actually updated?
        when (val result = balanceData.withdrawFromBalance(input)) {
            is OkOld -> {}
            is ErrOld -> {
                logger.log(Level.SEVERE, "error withdrawing from balance", result.error)
                return ErrOld(DatabaseErrorN)
            }
        }

        return when (val result = balanceData.getBalance(input.accountId)) {
            is OkOld -> {
                result.value?.let {
                    OkOld(result.value)
                } ?: run {
                    logger.log(Level.SEVERE, "balance for account id not found after withdrawing")
                    ErrOld(DatabaseErrorN) // NOTE: If there is no balance here, something went wrong which is why it's returning a DatabaseError
                }
            }
            is ErrOld -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                ErrOld(DatabaseErrorN)
            }
        }
    }

    fun depositIntoBalance(input: DepositIntoBalance): ResultOld<Balance, ErrorMessage> {
        // TODO: Add a check to make sure a row was actually updated?
        when (val result = balanceData.depositIntoBalance(input)) {
            is OkOld -> {}
            is ErrOld -> {
                logger.log(Level.SEVERE, "error depositing into balance", result.error)
                return ErrOld(DatabaseErrorN)
            }
        }

        return when (val result = balanceData.getBalance(input.accountId)) {
            is OkOld -> {
                result.value?.let {
                    OkOld(result.value)
                } ?: run {
                    logger.log(Level.SEVERE, "balance for account id not found after depositing")
                    ErrOld(DatabaseErrorN) // NOTE: If there is no balance here, something went wrong which is why it's returning a DatabaseError
                }
            }
            is ErrOld -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                ErrOld(DatabaseErrorN)
            }
        }
    }

    fun transferBalance(input: TransferBalance): ResultOld<Boolean, ErrorMessage> {
        // TODO: Verify fromBalance has enough for transfer
        return OkOld(true)
    }

    fun format(amount: Double): Component {
        var currencyName = currencyNamePlural()
        if (amount == 1.00) {
            currencyName = currencyNameSingular()
        }
        return Component.text("%.2f $currencyName".format(amount))
    }
}