package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.model.ResultA
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

    //fun createAccount(uuid: UUID): ResultA<Boolean> {
    //    return when (val result = accountData.createAccount(uuid)) {
    //        is Result.Success -> {
    //            result
    //        }
    //        is Result.Error -> {
    //            logger.log(Level.SEVERE, result.message, result.cause)
    //            ResultA.Error("unable to create an account", null)
    //        }
    //        else -> {
    //            logger.log(Level.SEVERE, "unexpected result from createAccount")
    //            ResultA.Error("unexpected result", null)
    //        }
    //    }
    //}

    //fun hasAccount(uuid: UUID): ResultA<Boolean> {
    //    return when (val result = accountData.getAccount(uuid)) {
    //        is Result.Success -> {
    //            ResultA.Success<Boolean>(true)
    //        }
    //        is Result.Info -> {
    //            ResultA.Info<Boolean>("account not found")
    //        }
    //        is Result.Error -> {
    //            logger.log(Level.SEVERE, result.message, result.cause)
    //            ResultA.Error("unable to check if account exists", null)
    //        }
    //    }
    //}
}