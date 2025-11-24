package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.data.entity.Balance
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.ericgrandt.totaleconomy.model.DepositIntoBalance
import com.ericgrandt.totaleconomy.model.ErrorMessage
import com.ericgrandt.totaleconomy.model.SetBalance
import com.ericgrandt.totaleconomy.model.WithdrawFromBalance
import com.ericgrandt.totaleconomy.result.Err
import com.ericgrandt.totaleconomy.result.Ok
import com.ericgrandt.totaleconomy.result.Result
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

    // TODO: Return created account?
    fun createAccount(uuid: UUID): Result<Int, ErrorMessage> {
        return when (val result = accountData.createAccount(uuid)) {
            is Ok -> {
                Ok(result.value)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error creating account", result.error)
                Err(DatabaseError)
            }
        }
    }

    fun hasAccount(uuid: UUID): Result<Boolean, ErrorMessage> {
        return when (val result = accountData.getAccount(uuid)) {
            is Ok -> {
                Ok(result.value != null)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting account", result.error)
                Err(DatabaseError)
            }
        }
    }

    fun getBalance(uuid: UUID): Result<Double, ErrorMessage> {
        return when (val result = balanceData.getBalance(uuid)) {
            is Ok -> {
                Ok(result.value?.balance ?: 0.00)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                Err(DatabaseError)
            }
        }
    }

    // TODO: Return updated balance?
    fun setBalance(input: SetBalance): Result<Int, ErrorMessage> {
        return when (val result = balanceData.setBalance(input)) {
            is Ok -> {
                Ok(result.value)
            }
            is Err -> {
                logger.log(Level.SEVERE, "error setting balance", result.error)
                Err(DatabaseError)
            }
        }
    }

    fun withdrawFromBalance(input: WithdrawFromBalance): Result<Balance, ErrorMessage> {
        // TODO: Add a check to make sure a row was actually updated?
        when (val result = balanceData.withdrawFromBalance(input)) {
            is Ok -> {}
            is Err -> {
                logger.log(Level.SEVERE, "error withdrawing from balance", result.error)
                return Err(DatabaseError)
            }
        }

        return when (val result = balanceData.getBalance(input.accountId)) {
            is Ok -> {
                result.value?.let {
                    Ok(result.value)
                } ?: run {
                    logger.log(Level.SEVERE, "balance for account id not found after withdrawing")
                    Err(DatabaseError) // NOTE: If there is no balance here, something went wrong which is why it's returning a DatabaseError
                }
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                Err(DatabaseError)
            }
        }
    }

    fun depositIntoBalance(input: DepositIntoBalance): Result<Balance, ErrorMessage> {
        // TODO: Add a check to make sure a row was actually updated?
        when (val result = balanceData.depositIntoBalance(input)) {
            is Ok -> {}
            is Err -> {
                logger.log(Level.SEVERE, "error depositing into balance", result.error)
                return Err(DatabaseError)
            }
        }

        return when (val result = balanceData.getBalance(input.accountId)) {
            is Ok -> {
                result.value?.let {
                    Ok(result.value)
                } ?: run {
                    logger.log(Level.SEVERE, "balance for account id not found after depositing")
                    Err(DatabaseError) // NOTE: If there is no balance here, something went wrong which is why it's returning a DatabaseError
                }
            }
            is Err -> {
                logger.log(Level.SEVERE, "error getting balance", result.error)
                Err(DatabaseError)
            }
        }
    }
}