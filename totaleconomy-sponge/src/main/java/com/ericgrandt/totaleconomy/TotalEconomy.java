package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.commands.BalanceCommand;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.config.PluginConfig;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.listeners.PlayerListener;
import com.google.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.ProvideServiceEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("totaleconomy")
public class TotalEconomy {
    private final PluginContainer container;
    private final Logger logger;
    private final ConfigurationReference<CommentedConfigurationNode> configurationReference;

    private PluginConfig config;
    private CurrencyDto defaultCurrency;
    private EconomyImpl economy;

    @Inject
    private TotalEconomy(
        final PluginContainer container,
        final Logger logger,
        final @DefaultConfig(sharedRoot = false) ConfigurationReference<CommentedConfigurationNode> configurationReference
    ) {
        this.container = container;
        this.logger = logger;
        this.configurationReference = configurationReference;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        try {
            ValueReference<PluginConfig, CommentedConfigurationNode> valueReference = configurationReference
                .referenceTo(PluginConfig.class);
            configurationReference.save();

            config = valueReference.get();
            if (config == null) {
                logger.error("[TotalEconomy] Configuration is null. Plugin not started.");
                return;
            }
        } catch (final ConfigurateException ex) {
            logger.error("[TotalEconomy] Error loading configuration", ex);
            return;
        }

        Database database = new Database(
            config.getDatabaseUrl(),
            config.getDatabaseUser(),
            config.getDatabasePassword()
        );

        try {
            database.initDatabase();
        } catch (SQLException | IOException e) {
            logger.error(
                "[Total Economy] Error calling initDatabase",
                e
            );
            return;
        }

        CurrencyData currencyData = new CurrencyData(database);

        try {
            defaultCurrency = currencyData.getDefaultCurrency();
        } catch (SQLException e) {
            logger.error(
                "[Total Economy] Unable to load default currency",
                e
            );
            return;
        }

        AccountData accountData = new AccountData(database);
        BalanceData balanceData = new BalanceData(database);

        economy = new EconomyImpl(logger, defaultCurrency, accountData, balanceData);

        registerListeners();
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        Command.Parameterized balanceCommand = Command.builder()
            .executor(new BalanceCommand(economy, economy.defaultCurrency()))
            .build();
        event.register(
            container,
            balanceCommand,
            "balance"
        );
    }

    @Listener
    public void registerEconomyService(ProvideServiceEvent.EngineScoped<EconomyImpl> event) {
        event.suggest(() -> economy);
    }

    private void registerListeners() {
        Sponge.eventManager().registerListeners(container, new PlayerListener(economy));
    }
}
