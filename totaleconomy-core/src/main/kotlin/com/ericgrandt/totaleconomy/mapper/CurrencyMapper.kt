package com.ericgrandt.totaleconomy.mapper

import com.ericgrandt.totaleconomy.data.table.CurrencyTable
import com.ericgrandt.totaleconomy.model.TECurrency
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toTECurrency(): TECurrency {
    return TECurrency(
        code = this[CurrencyTable.code],
        name = this[CurrencyTable.name],
        pluralName = this[CurrencyTable.pluralName],
        symbol = this[CurrencyTable.symbol],
        fractionalDigits = this[CurrencyTable.fractionalDigits],
        isDefault = this[CurrencyTable.isDefault],
    )
}
