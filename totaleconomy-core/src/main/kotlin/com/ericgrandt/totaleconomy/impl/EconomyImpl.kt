package com.ericgrandt.totaleconomy.impl

import com.ericgrandt.totaleconomy.EconomyProvider
import com.ericgrandt.totaleconomy.model.Account
import com.ericgrandt.totaleconomy.model.TEAccount
import java.math.BigDecimal
import java.util.UUID

class EconomyImpl : EconomyProvider {
    override fun createAccount(
        accountId: UUID,
        currencyCode: String,
        startingBalance: BigDecimal,
    ): Account {
        // accountData.createAccount(accountId).mapBoth(
        //    success = {
        //        it
        //    },
        //    failure = {},
        // )
        return TEAccount(UUID.randomUUID(), "", BigDecimal.ZERO)
    }
}
