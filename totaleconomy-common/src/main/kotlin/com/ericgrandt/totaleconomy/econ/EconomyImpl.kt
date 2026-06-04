package com.ericgrandt.totaleconomy.econ

import com.ericgrandt.totaleconomy.EconomyProvider
import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.model.Account
import com.github.michaelbull.result.mapBoth
import java.math.BigDecimal
import java.util.UUID

class EconomyImpl : EconomyProvider {
    val accountData: AccountData

    constructor(accountData: AccountData) {
        this.accountData = accountData
    }

    override fun createAccount(
        accountId: UUID,
        currencyCode: String,
        startingBalance: BigDecimal,
    ): Account {
        accountData.createAccountOld(accountId).mapBoth(
            success = {
                it
            },
            failure = {},
        )
        return TEAccount(UUID.randomUUID(), "", BigDecimal.ZERO, 1L)
    }
}

data class TEAccount(
    override val id: UUID,
    override val currencyCode: String,
    override val balance: BigDecimal,
    val createdAt: Long,
) : Account
