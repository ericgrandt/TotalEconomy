package com.ericgrandt.totaleconomy.command

import com.ericgrandt.totaleconomy.impl.PaperPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BalanceCommandExecutor(
    private val scope: CoroutineScope,
    private val balanceCommand: BalanceCommand,
) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) {
            return false
        }

        scope.launch {
            balanceCommand.execute(PaperPlayer(sender), mapOf())
        }

        return true
    }
}
