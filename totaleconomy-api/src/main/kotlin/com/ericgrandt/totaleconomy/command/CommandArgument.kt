package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.model.Player

sealed class CommandArgument {
    data class DoubleParam(
        val value: Double,
    ) : CommandArgument()

    data class PlayerParam(
        val value: Player,
    ) : CommandArgument()

    data class StringParam(
        val value: String,
    ) : CommandArgument()
}
