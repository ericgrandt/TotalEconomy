package com.ericgrandt.totaleconomy

import com.ericgrandt.totaleconomy.command.BalanceCommand
import com.ericgrandt.totaleconomy.command.BalanceCommandExecutor
import com.ericgrandt.totaleconomy.data.AccountData
import com.ericgrandt.totaleconomy.data.BalanceData
import com.ericgrandt.totaleconomy.econ.CommonEconomy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.v1.jdbc.Database
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger
import com.ericgrandt.totaleconomy.data.Database as TEDatabase

class TotalEconomy :
    JavaPlugin(),
    Listener {
    private val pluginScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger: Logger = Logger.getLogger("Total Economy")
    private lateinit var economy: CommonEconomy

    override fun onEnable() {
        saveDefaultConfig()

        val database =
            TEDatabase(
                config.getString("database.url").toString(),
                config.getString("database.user").toString(),
                config.getString("database.password").toString(),
            )

        try {
            Database.connect(database.dataSource)
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

        val accountData = AccountData()
        val balanceData = BalanceData()
        economy = CommonEconomy(accountData, balanceData)

        registerCommands()
    }

    override fun onDisable() {
        pluginScope.cancel()
    }

    private fun registerCommands() {
        getCommand("balance")?.setExecutor(BalanceCommandExecutor(pluginScope, BalanceCommand(economy)))
    }
}
