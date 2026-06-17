package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.economy.EconomyProvider
import com.ericgrandt.totaleconomy.mapper.getMessage
import com.ericgrandt.totaleconomy.model.Player
import com.ericgrandt.totaleconomy.model.Sender
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component

class BalanceCommand(
    private val economy: EconomyProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : Command {
    override suspend fun execute(
        sender: Sender,
        args: Map<String, CommandArgument>,
    ): CommandResult {
        val player = sender as? Player ?: return CommandResult.FAILURE

        return withContext(dispatcher) {
            try {
                val currencyCodeParam = (args["currencyCode"] as? CommandArgument.StringParam)?.value
                val currency =
                    if (currencyCodeParam != null) {
                        economy.getCurrency(currencyCodeParam)
                    } else {
                        economy.getDefaultCurrency()
                    }

                val account = economy.getAccount(player.uniqueId, currency.code)

                player.sendMessage(Component.text("Balance: ").append(currency.format(account.balance)))
                CommandResult.SUCCESS
            } catch (e: Exception) {
                player.sendMessage(e.getMessage())
                CommandResult.FAILURE
            }
        }
    }
}
