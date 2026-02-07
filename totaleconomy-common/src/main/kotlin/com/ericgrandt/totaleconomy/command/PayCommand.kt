package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.game.CommonPlayer
import com.ericgrandt.totaleconomy.game.CommonSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

class PayCommand : CommonCommand {
    private val economy: CommonEconomy

    constructor(economy: CommonEconomy) {
        this.economy = economy
    }

    override fun runAsync(
        scope: CoroutineScope,
        sender: CommonSender,
        args: MutableMap<String, CommonParameter<*>>
    ): Boolean {
        if (sender !is CommonPlayer || args["toPlayer"]?.value !is CommonPlayer || args["amount"]?.value !is Double) {
            return false
        }

        val toPlayer = args["toPlayer"]?.value as CommonPlayer
        val amount = args["amount"]?.value as Double

        scope.launch {
            execute(sender, toPlayer, amount)
        }

        return true
    }

    private fun execute(fromPlayer: CommonPlayer, toPlayer: CommonPlayer, amount: Double) {
    }
}