package com.ericgrandt;

import com.ericgrandt.commands.CommandRegister;
import com.ericgrandt.config.DefaultConfiguration;
import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.data.Database;
import com.ericgrandt.player.PlayerListener;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.google.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetId;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

@Plugin("totaleconomy")
public class TotalEconomy {
    private final Logger logger;

    private final ConfigurationReference<CommentedConfigurationNode> reference;
    private ValueReference<DefaultConfiguration, CommentedConfigurationNode> config;

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    @AssetId("schema/mysql.sql")
    private Asset mysqlSchema;

    private static TotalEconomy plugin;
    private Database database;
    private TEEconomyService economyService;
    private AccountService accountService;

    @Inject
    public TotalEconomy(final Logger logger, final @DefaultConfig(sharedRoot = false) ConfigurationReference<CommentedConfigurationNode> reference) {
        plugin = this;
        this.logger = logger;
        this.reference = reference;
    }

    @Listener
    public void onConstructPlugin(ConstructPluginEvent event) {
        try {
            config = reference.referenceTo(DefaultConfiguration.class);
            this.reference.save();
        } catch (final ConfigurateException ex) {
            logger.error("Unable to load test configuration", ex);
        }

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
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        CommandRegister commandRegister = new CommandRegister(pluginContainer, economyService, accountService);
        commandRegister.registerBalanceCommand(event);
    }

    @Listener
    public void onServerStarted(StartedEngineEvent<Server> event) {
        logger.info("TotalEconomy started successfully");
    }

    public static TotalEconomy getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public ValueReference<DefaultConfiguration, CommentedConfigurationNode> getDefaultConfiguration() {
        return config;
    }

    public Database getDatabase() {
        return database;
    }

    public TEEconomyService getEconomyService() {
        return economyService;
    }

    public Asset getMysqlSchema() {
        return mysqlSchema;
    }
}
