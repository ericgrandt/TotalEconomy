package com.ericgrandt.totaleconomy.data.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object CurrencyTable : Table("te_currency") {
    val id = integer("id").autoIncrement()
    val code = varchar("code", length = 10).uniqueIndex()
    val name = varchar("name", length = 20).uniqueIndex()
    val pluralName = varchar("plural_name", length = 20).uniqueIndex()
    val symbol = varchar("symbol", length = 1).nullable()
    val fractionalDigits = integer("fractional_digits")
    val isDefault = bool("is_default")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
