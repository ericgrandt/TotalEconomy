package com.ericgrandt.totaleconomy

import com.ericgrandt.totaleconomy.data.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

class TotalEconomy : JavaPlugin(), Listener {
    private val pluginScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger: Logger = Logger.getLogger("Minecraft")

    override fun onEnable() {
        saveDefaultConfig()

        val database = Database(
            config.getString("database.url").toString(),
            config.getString("database.user").toString(),
            config.getString("database.password").toString()
        )

        try {
            database.initDatabase()
        } catch (e: SQLException) {
            logger.log(
                Level.SEVERE,
                "[Total Economy] Error initializing database",
                e
            )
            server.pluginManager.disablePlugin(this)
            return
        }
    }

    override fun onDisable() {
        pluginScope.cancel()
    }
}