package com.erigitic;

import com.erigitic.commands.CommandRegister;
import com.erigitic.config.DefaultConfiguration;
import com.erigitic.data.AccountService;
import com.erigitic.data.Database;
import com.erigitic.economy.TEEconomyService;
import com.erigitic.player.PlayerListener;
import com.google.inject.Inject;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;

import java.io.File;
import java.io.IOException;

@Plugin(id="totaleconomy", name="Total Economy", version="2.0.0", description="All in one economy plugin for Minecraft and Sponge")
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

    private EventContext eventContext;

    private static TotalEconomy plugin;
    private DefaultConfiguration defaultConfiguration;
    private Database database;
    private TEEconomyService economyService;

    public TotalEconomy() {
        plugin = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build();
        copyResourcesToConfigDirectory();

        defaultConfiguration = new DefaultConfiguration(this);

        // AccountService accountService = new AccountService(null);

        // database = new Database();
        // database.setup();
        //
        // economyService = new TEEconomyService();
        // Sponge.getServiceManager().setProvider(this, EconomyService.class, economyService);
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        CommandRegister commandRegister = new CommandRegister();
        commandRegister.registerCommands();

        Sponge.getEventManager().registerListeners(this, new PlayerListener());
    }

    private void copyResourcesToConfigDirectory() {
        File config = new File(configDir, "totaleconomy.conf");
        Asset defaultConf = pluginContainer.getAsset("totaleconomy.conf").get();

        try {
            defaultConf.copyToFile(config.toPath(), false);
        } catch (IOException e) {
            logger.error("Configuration files could not be copied");
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("TotalEconomy started successfully");
    }

    public EventContext getEventContext() {
        return eventContext;
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

    public Database getDatabase() {
        return database;
    }

    public TEEconomyService getEconomyService() {
        return economyService;
    }
}
