package com.ericgrandt.totaleconomy

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class TotalEconomy : JavaPlugin(), Listener {
    override fun onEnable() {
        saveDefaultConfig()
    }
}