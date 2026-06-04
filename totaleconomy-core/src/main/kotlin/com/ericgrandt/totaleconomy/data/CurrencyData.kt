package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.table.CurrencyTable
import com.ericgrandt.totaleconomy.mapper.toTECurrency
import com.ericgrandt.totaleconomy.model.TECurrency
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.v1.jdbc.selectAll

class CurrencyData {
    fun getCurrencyList(): Result<List<TECurrency>, Throwable> {
        return runCatching {
            CurrencyTable.selectAll().map {
                it.toTECurrency()
            }
        }
    }
}
