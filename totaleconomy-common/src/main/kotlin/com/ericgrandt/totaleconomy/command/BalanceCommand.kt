package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.game.CommonPlayer
import com.ericgrandt.totaleconomy.game.CommonSender
import com.ericgrandt.totaleconomy.model.errorToUserMessage
import com.github.michaelbull.result.mapBoth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component

class BalanceCommand : CommonCommand {
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
        economy.getBalance(player.getUniqueID()).mapBoth(
            success = {
                player.sendMessage(Component.text("You have ").append(economy.format(it)))
            },
            failure = {
                val userMessage = errorToUserMessage(it)
                player.sendMessage(Component.text(userMessage))
            }
        )
    }
}