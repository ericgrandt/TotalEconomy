package com.ericgrandt.totaleconomy.data.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object AccountTable : Table("te_account") {
    val id = integer("id").autoIncrement()
    val playerId = varchar("player_id", length = 36)
    val currencyCode = varchar("currency_code", length = 10).references(CurrencyTable.code)
    val balance = double("balance")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(playerId, currencyCode)
    }
}
