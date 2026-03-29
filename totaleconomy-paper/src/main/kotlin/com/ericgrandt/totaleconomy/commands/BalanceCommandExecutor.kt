package com.ericgrandt.totaleconomy.commands

import com.ericgrandt.totaleconomy.command.BalanceCommand
import com.ericgrandt.totaleconomy.impl.BukkitPlayer
import kotlinx.coroutines.CoroutineScope
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BalanceCommandExecutor : CommandExecutor{
    private val scope: CoroutineScope
    private val balanceCommand: BalanceCommand

    constructor(scope: CoroutineScope, balanceCommand: BalanceCommand) {
        this.scope = scope
        this.balanceCommand = balanceCommand
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            return false
        }

        return balanceCommand.runAsync(scope, BukkitPlayer(sender), mutableMapOf())
    }
}