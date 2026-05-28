package com.ericgrandt.totaleconomy.data.table

import org.jetbrains.exposed.v1.core.Table

object BalanceTable : Table("te_budget") {
    val id = varchar("id", length = 36)
    val accountId = reference("account_id", AccountTable.id)
    val balance = double("balance")

    override val primaryKey = PrimaryKey(AccountTable.id)
}
