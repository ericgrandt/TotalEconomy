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
import com.ericgrandt.totaleconomy.result.Err
import com.ericgrandt.totaleconomy.result.Ok
import com.github.michaelbull.result.Result
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

    fun createAccountOld(uuid: UUID): ResultOld<Int, ErrorMessage> {
        return when (val result = accountData.createAccountOld(uuid)) {
            is Ok -> {
                Ok(result.value)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error creating account", result.error)
                Err(DatabaseErrorN)
            }
        }
    }

    fun createAccount(uuid: UUID): Result<Int, DomainError> {
        return accountData.createAccount(uuid).mapError {
            logger.log(Level.SEVERE, "error creating account", it)
            DatabaseError
        }
    }

    fun hasAccount(uuid: UUID): ResultOld<Boolean, ErrorMessage> {
        return when (val result = accountData.getAccount(uuid)) {
            is Ok -> {
                Ok(result.value != null)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting account", result.error)
                Err(DatabaseErrorN)
            }
        }
    }

    fun getBalance(uuid: UUID): ResultOld<Double, ErrorMessage> {
        return when (val result = balanceData.getBalance(uuid)) {
            is Ok -> {
                Ok(result.value?.balance ?: 0.00)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                Err(DatabaseErrorN)
            }
        }
    }

    // TODO: Return updated balance?
    fun setBalance(input: SetBalance): ResultOld<Int, ErrorMessage> {
        return when (val result = balanceData.setBalance(input)) {
            is Ok -> {
                Ok(result.value)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error setting balance", result.error)
                Err(DatabaseErrorN)
            }
        }
    }

    fun withdrawFromBalance(input: WithdrawFromBalance): ResultOld<Balance, ErrorMessage> {
        // TODO: Add a check to make sure a row was actually updated?
        when (val result = balanceData.withdrawFromBalance(input)) {
            is Ok -> {}
            is Err -> {
                logger.log(Level.SEVERE, "error withdrawing from balance", result.error)
                return Err(DatabaseErrorN)
            }
        }

        return when (val result = balanceData.getBalance(input.accountId)) {
            is Ok -> {
                result.value?.let {
                    Ok(result.value)
                } ?: run {
                    logger.log(Level.SEVERE, "balance for account id not found after withdrawing")
                    Err(DatabaseErrorN) // NOTE: If there is no balance here, something went wrong which is why it's returning a DatabaseError
                }
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                Err(DatabaseErrorN)
            }
        }
    }

    fun depositIntoBalance(input: DepositIntoBalance): ResultOld<Balance, ErrorMessage> {
        // TODO: Add a check to make sure a row was actually updated?
        when (val result = balanceData.depositIntoBalance(input)) {
            is Ok -> {}
            is Err -> {
                logger.log(Level.SEVERE, "error depositing into balance", result.error)
                return Err(DatabaseErrorN)
            }
        }

        return when (val result = balanceData.getBalance(input.accountId)) {
            is Ok -> {
                result.value?.let {
                    Ok(result.value)
                } ?: run {
                    logger.log(Level.SEVERE, "balance for account id not found after depositing")
                    Err(DatabaseErrorN) // NOTE: If there is no balance here, something went wrong which is why it's returning a DatabaseError
                }
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                Err(DatabaseErrorN)
            }
        }
    }

    fun transferBalance(input: TransferBalance): ResultOld<Boolean, ErrorMessage> {
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