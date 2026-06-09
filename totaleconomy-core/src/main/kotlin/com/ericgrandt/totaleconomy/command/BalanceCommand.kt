package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.economy.Economy
import com.ericgrandt.totaleconomy.model.Player
import com.ericgrandt.totaleconomy.model.Sender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class BalanceCommand(
    private val economy: Economy,
) : Command {
    override suspend fun execute(
        sender: Sender,
        args: Map<String, CommandArgument>,
    ): CommandResult {
        if (sender !is Player) {
            return CommandResult.FAILURE
        }

        var currencyCode = (args["currencyCode"] as? CommandArgument.StringParam)?.value
        if (currencyCode == null) {
            // TODO: Get default currency code
            currencyCode = "USD"
        }

        withContext(Dispatchers.IO) {
            val account = economy.getAccount(UUID.randomUUID(), currencyCode)
        }

        return CommandResult.SUCCESS
    }
}
