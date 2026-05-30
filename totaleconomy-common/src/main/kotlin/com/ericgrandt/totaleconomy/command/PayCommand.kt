package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.game.CommonPlayer
import com.ericgrandt.totaleconomy.game.CommonSender
import com.ericgrandt.totaleconomy.model.TransferBalance
import com.ericgrandt.totaleconomy.model.errorToUserMessage
import com.github.michaelbull.result.mapBoth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component

class PayCommand : CommonCommand {
    private val economy: CommonEconomy

    constructor(economy: CommonEconomy) {
        this.economy = economy
    }

    override fun runAsync(
        scope: CoroutineScope,
        sender: CommonSender,
        args: Map<String, CommonParameter>,
    ): Boolean {
        val toPlayer = (args["toPlayer"] as? CommonParameter.PlayerParam)?.value
        val amount = (args["amount"] as? CommonParameter.DoubleParam)?.value

        if (sender !is CommonPlayer || toPlayer == null || amount == null) {
            return false
        }

        scope.launch {
            execute(sender, toPlayer, amount)
        }

        return true
    }

    private fun execute(
        fromPlayer: CommonPlayer,
        toPlayer: CommonPlayer,
        amount: Double,
    ) {
        val input = TransferBalance(fromPlayer.getUniqueID(), toPlayer.getUniqueID(), amount)
        economy.transferBalance(input).mapBoth(
            success = {
                fromPlayer.sendMessage(
                    Component.text("You paid " + toPlayer.getName() + " ").append(economy.format(amount)),
                )
                toPlayer.sendMessage(
                    Component.text("You received ").append(economy.format(amount)).append(
                        Component.text(" from " + fromPlayer.getName()),
                    ),
                )
            },
            failure = {
                // TODO: Test
                val userMessage = errorToUserMessage(it)
                fromPlayer.sendMessage(Component.text(userMessage))
            },
        )
    }
}
