package com.ericgrandt.totaleconomy.commands

import com.ericgrandt.totaleconomy.command.CommonParameter
import com.ericgrandt.totaleconomy.command.PayCommand
import com.ericgrandt.totaleconomy.impl.BukkitPlayer
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PayCommandExecutor : CommandExecutor {
    private val scope: CoroutineScope
    private val payCommand: PayCommand

    constructor(scope: CoroutineScope, payCommand: PayCommand) {
        this.scope = scope
        this.payCommand = payCommand
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        var toPlayer = Bukkit.getPlayer(args[0])
        var amount = args[1].toDoubleOrNull()

        if (sender !is Player || toPlayer == null || amount == null) {
            return false
        }

        var argMap = mutableMapOf<String, CommonParameter>()
        argMap["toPlayer"] = CommonParameter.PlayerParam(BukkitPlayer(toPlayer))
        argMap["amount"] = CommonParameter.DoubleParam(amount)

        return payCommand.runAsync(scope, BukkitPlayer(sender), argMap)
    }
}
