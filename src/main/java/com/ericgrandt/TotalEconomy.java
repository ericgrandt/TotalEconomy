package com.ericgrandt;

import com.ericgrandt.commands.CommandRegister;
import com.ericgrandt.config.DefaultConfiguration;
import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.data.Database;
import com.ericgrandt.player.PlayerListener;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;
import com.ericgrandt.wrappers.CommandBuilder;
import com.ericgrandt.wrappers.ParameterWrapper;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.ProvideServiceEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("totaleconomy")
public class TotalEconomy {
    private final Logger logger = LogManager.getLogger("TotalEconomy");

    private final ConfigurationReference<CommentedConfigurationNode> configurationReference;
    private ValueReference<DefaultConfiguration, CommentedConfigurationNode> config;

    private final PluginContainer pluginContainer;

    private Database database;
    private TEEconomyService economyService;
    private AccountService accountService;
    private CurrencyData currencyData;

    @Inject
    public TotalEconomy(
        final PluginContainer pluginContainer,
        final @DefaultConfig(sharedRoot = false) ConfigurationReference<CommentedConfigurationNode> configurationReference
    ) {
        this.pluginContainer = pluginContainer;
        this.configurationReference = configurationReference;
    }

    @Listener
    public void onConstructPlugin(ConstructPluginEvent event) {
        try {
            config = configurationReference.referenceTo(DefaultConfiguration.class);
            this.configurationReference.save();
        } catch (final ConfigurateException ex) {
            logger.error("Unable to load test configuration", ex);
        }

        database = new Database(logger, pluginContainer);
        database.setup();

        AccountData accountData = new AccountData(logger, database);
        currencyData = new CurrencyData(logger, database);

        accountService = new AccountService(accountData);
        economyService = new TEEconomyService(accountData, currencyData);
    }

    @Listener
    public void onServerStarting(final StartingEngineEvent<Server> event) {
        Sponge.eventManager().registerListeners(pluginContainer, new PlayerListener(economyService));
    }

    @Listener
    public void onProvideService(ProvideServiceEvent<EconomyService> event) {
        event.suggest(() -> economyService);

        Sponge.game().findRegistry(RegistryTypes.CURRENCY).ifPresent(registry -> {
        	currencyData.getCurrencies().forEach(currency -> {
        		registry.register(registry.type().location(), currency);
        	});
        });
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        CommandRegister commandRegister = new CommandRegister(
            pluginContainer,
            economyService,
            accountService,
            new CommandBuilder(),
            new ParameterWrapper()
        );
        commandRegister.registerCommands(event);
    }

    @Listener
    public void onServerStarted(StartedEngineEvent<Server> event) {
        logger.info("TotalEconomy started successfully");
    }

    public ValueReference<DefaultConfiguration, CommentedConfigurationNode> getDefaultConfiguration() {
        return config;
    }

    public Database getDatabase() {
        return database;
    }
}
