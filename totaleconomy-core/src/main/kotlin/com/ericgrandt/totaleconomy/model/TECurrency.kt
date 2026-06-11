package com.ericgrandt.totaleconomy.model

import net.kyori.adventure.text.Component
import java.math.BigDecimal
import java.math.RoundingMode

data class TECurrency(
    override val code: String,
    override val name: String,
    override val pluralName: String,
    override val symbol: String?,
    override val fractionalDigits: Int,
    override val isDefault: Boolean,
) : Currency {
    // TODO: Test
    override fun format(amount: BigDecimal): Component {
        val balance = amount.setScale(fractionalDigits, RoundingMode.DOWN)

        if (symbol == null) {
            val suffix = if (balance.compareTo(BigDecimal.ONE) == 0) name else pluralName
            return Component.text("${balance.toPlainString()} $suffix")
        }

        return Component.text("$symbol${balance.toPlainString()}")
    }
}
