package com.ericgrandt.totaleconomy

import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.Database
import com.ericgrandt.totaleconomy.economy.Economy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException

class TotalEconomy :
    JavaPlugin(),
    Listener {
    private val pluginScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger: Logger = LoggerFactory.getLogger("Total Economy")
    private lateinit var economy: Economy

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
            logger.error("Error initializing database", e)
            server.pluginManager.disablePlugin(this)
            return
        }

        val accountData = AccountData()
        economy = Economy(logger, accountData)

        // registerCommands()
    }

    override fun onDisable() {
        pluginScope.cancel()
    }

    private fun registerCommands() {
        // getCommand("balance")?.setExecutor(BalanceCommandExecutor(pluginScope, BalanceCommand(economy)))
    }
}
