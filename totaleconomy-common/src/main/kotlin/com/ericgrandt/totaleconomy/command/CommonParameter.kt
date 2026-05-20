package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.game.CommonPlayer

sealed class CommonParameter {
    data class DoubleParam(
        val value: Double,
    ) : CommonParameter()

    data class PlayerParam(
        val value: CommonPlayer,
    ) : CommonParameter()
}
