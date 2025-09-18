package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.model.Result
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger

class CommonEconomy {
    val accountData: AccountData

    companion object {
        val logger: Logger = Logger.getLogger(CommonEconomy::class.java.name)
    }

    constructor(accountData: AccountData) {
        this.accountData = accountData
    }

    fun createAccount(uuid: UUID): Result<Boolean> {
        return when (val result = accountData.createAccount(uuid)) {
            is Result.Success -> {
                result
            }
            is Result.Error -> {
                logger.log(Level.SEVERE, result.message, result.cause)
                Result.Error("unable to create an account", null)
            }
            else -> {
                logger.log(Level.SEVERE, "unexpected result from createAccount")
                Result.Error("unexpected result", null)
            }
        }
    }

    fun hasAccount(uuid: UUID): Result<Boolean> {
        return Result.Success(true)
    }
}