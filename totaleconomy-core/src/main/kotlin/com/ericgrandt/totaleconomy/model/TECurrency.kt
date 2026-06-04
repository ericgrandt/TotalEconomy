package com.ericgrandt.totaleconomy.model

import net.kyori.adventure.text.Component
import java.math.BigDecimal

data class TECurrency(
    override val code: String,
    override val name: String,
    override val pluralName: String,
    override val symbol: String?,
    override val fractionalDigits: Int,
    override val isDefault: Boolean,
) : Currency {
    override fun format(amount: BigDecimal): Component {
        TODO("Not yet implemented")
    }
}
