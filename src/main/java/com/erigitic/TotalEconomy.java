package com.erigitic;

import com.erigitic.commands.CommandRegister;
import com.erigitic.config.DefaultConfiguration;
import com.erigitic.data.AccountData;
import com.erigitic.data.CurrencyData;
import com.erigitic.data.Database;
import com.erigitic.player.PlayerListener;
import com.erigitic.services.AccountService;
import com.erigitic.services.TEEconomyService;
import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin("totaleconomy")
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
    private TEEconomyService economyService;
    private AccountService accountService;

    public TotalEconomy() {
        plugin = this;
    }

    @Listener
    public void onConstructPlugin(ConstructPluginEvent event) {
        copyResourcesToConfigDirectory();

        defaultConfiguration = new DefaultConfiguration(this);

        database = new Database();
        database.setup();

        AccountData accountData = new AccountData(database);
        CurrencyData currencyData = new CurrencyData(database);

        accountService = new AccountService(accountData);
        economyService = new TEEconomyService(accountData, currencyData);
        // Sponge.getServiceManager().setProvider(this, EconomyService.class, economyService);
    }

    @Listener
    public void onServerStarting(final StartingEngineEvent<Server> event) {
        Sponge.eventManager().registerListeners(pluginContainer, new PlayerListener());
        Sponge.eventManager().registerListeners(pluginContainer, new CommandRegister(pluginContainer, economyService, accountService));
    }

    @Listener
    public void onServerStarted(StartedEngineEvent<Server> event) {
        logger.info("TotalEconomy started successfully");
    }

    private void copyResourcesToConfigDirectory() {
        File config = new File(configDir, "totaleconomy.conf");
        Asset defaultConf = Sponge.assetManager().asset("totaleconomy.conf").get();

        try {
            defaultConf.copyToFile(config.toPath(), false);
        } catch (IOException e) {
            logger.error("Configuration files could not be copied");
        }
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
