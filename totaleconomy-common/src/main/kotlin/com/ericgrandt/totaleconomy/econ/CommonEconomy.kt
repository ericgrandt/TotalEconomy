package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.model.DatabaseError
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

    fun createAccount(uuid: UUID): Result<Boolean, ErrorMessage> {
        return when (val result = accountData.createAccount(uuid)) {
            is Ok -> {
                Ok(true)
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
                logger.log(Level.SEVERE, "", result.error)
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
                logger.log(Level.SEVERE, "", result.error)
                Err(DatabaseError)
            }
        }
    }

    fun setBalance(input: SetBalance): Result<Int, ErrorMessage> {
        return when (val result = balanceData.setBalance(input)) {
            is Ok -> {
                Ok(result.value)
            }
            is Err -> {
                logger.log(Level.SEVERE, "", result.error)
                Err(DatabaseError)
            }
        }
    }

    // TODO: Maybe have this return the updated balance?
    fun withdrawFromBalance(input: WithdrawFromBalance): Result<Int, ErrorMessage> {
        return when (val result = balanceData.withdrawFromBalance(input)) {
            is Ok -> {
                // TODO: Call get balance
                Ok(result.value)
            }
            is Err -> {
                logger.log(Level.SEVERE, "", result.error)
                Err(DatabaseError)
            }
        }
    }
}