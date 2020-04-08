package com.erigitic;

import com.erigitic.config.DefaultConfiguration;
import com.erigitic.data.Database;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;

@Plugin(id = "totaleconomy", name = "Total Economy", version = "2.0.0", description = "All in one economy plugin for Minecraft and Sponge")
public class TotalEconomy {
    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File config;

    @Inject
    private PluginContainer pluginContainer;

    private static TotalEconomy plugin;
    private DefaultConfiguration defaultConfiguration;
    private Database database;

    public TotalEconomy() {
        plugin = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        defaultConfiguration = new DefaultConfiguration();
        defaultConfiguration.loadConfiguration();

        database = new Database();
        database.setup();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("TotalEconomy started successfully");
    }

    public static TotalEconomy getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getConfigDir() {
        return configDir;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public DefaultConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }
}
