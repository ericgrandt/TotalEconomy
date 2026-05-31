package com.ericgrandt.totaleconomy.data.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object BankTable : Table("te_bank") {
    val id = varchar("id", length = 36)
    val accountId = reference("account_id", AccountTable.id)
    val balance = double("balance").default(0.0)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(AccountTable.id)

    init {
        uniqueIndex("uk_bank_account_id", accountId)
    }
}
