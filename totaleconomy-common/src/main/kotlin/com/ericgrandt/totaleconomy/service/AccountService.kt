package com.ericgrandt.totaleconomy.service

import com.ericgrandt.totaleconomy.data.AccountData
import java.util.logging.Logger

class AccountService {
    val accountData: AccountData

    companion object {
        val logger: Logger = Logger.getLogger(AccountService::class.java.name)
    }

    constructor(accountData: AccountData) {
        this.accountData = accountData
    }
}
