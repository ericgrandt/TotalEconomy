package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.model.DatabaseError
import com.ericgrandt.totaleconomy.model.ErrorMessage
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
}