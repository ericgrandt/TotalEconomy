package com.ericgrandt.totaleconomy.command

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
        val toPlayer = Bukkit.getPlayer(args[0])
        val amount = args[1].toDoubleOrNull()

        if (sender !is Player || toPlayer == null || amount == null) {
            return false
        }

        val argMap = mutableMapOf<String, CommonParameter>()
        argMap["toPlayer"] = CommonParameter.PlayerParam(BukkitPlayer(toPlayer))
        argMap["amount"] = CommonParameter.DoubleParam(amount)

        return payCommand.runAsync(scope, BukkitPlayer(sender), argMap)
    }
}
