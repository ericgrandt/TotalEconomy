package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.game.CommonPlayer
import com.ericgrandt.totaleconomy.game.CommonSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
        if (sender !is CommonPlayer) {
            return false
        }

        scope.launch {
            execute(sender)
        }

        return true
    }

    private fun execute(player: CommonPlayer) {
    }
}