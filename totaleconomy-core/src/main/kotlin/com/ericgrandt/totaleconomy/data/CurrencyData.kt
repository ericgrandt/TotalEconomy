package com.ericgrandt.totaleconomy.data

import com.ericgrandt.totaleconomy.data.table.CurrencyTable
import com.ericgrandt.totaleconomy.mapper.toTECurrency
import com.ericgrandt.totaleconomy.model.TECurrency
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll

class CurrencyData {
    fun getDefaultCurrency(): Result<TECurrency, Throwable> {
        return runCatching {
            CurrencyTable
                .selectAll()
                .where { CurrencyTable.isDefault eq true }
                .single()
                .toTECurrency()
        }
    }

    fun getCurrencyList(): Result<List<TECurrency>, Throwable> {
        return runCatching {
            CurrencyTable.selectAll().map {
                it.toTECurrency()
            }
        }
    }
}
