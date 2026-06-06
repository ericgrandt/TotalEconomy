package com.ericgrandt.totaleconomy

import com.ericgrandt.totaleconomy.data.Database
import com.ericgrandt.totaleconomy.impl.EconomyImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

class TotalEconomy :
    JavaPlugin(),
    Listener {
    private val pluginScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger: Logger = Logger.getLogger("Total Economy")
    private lateinit var economy: EconomyImpl

    override fun onEnable() {
        saveDefaultConfig()

        val database =
            Database(
                config.getString("database.url").toString(),
                config.getString("database.user").toString(),
                config.getString("database.password").toString(),
            )

        try {
            database.connect()
            database.initDatabase()
        } catch (e: SQLException) {
            logger.log(
                Level.SEVERE,
                "[Total Economy] Error initializing database",
                e,
            )
            server.pluginManager.disablePlugin(this)
            return
        }

        // val accountData = AccountData()
        // val balanceData = BalanceData()
        // economy = VaultImpl(accountData, balanceData)

        // registerCommands()
    }

    override fun onDisable() {
        pluginScope.cancel()
    }

    private fun registerCommands() {
        // getCommand("balance")?.setExecutor(BalanceCommandExecutor(pluginScope, BalanceCommand(economy)))
    }
}
