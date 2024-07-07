package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.commands.BalanceCommandExecutor;
import com.ericgrandt.totaleconomy.commands.JobCommandExecutor;
import com.ericgrandt.totaleconomy.commands.PayCommandExecutor;
import com.ericgrandt.totaleconomy.common.command.JobCommand;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import com.ericgrandt.totaleconomy.common.listeners.CommonPlayerListener;
import com.ericgrandt.totaleconomy.common.services.JobService;
import com.ericgrandt.totaleconomy.commonimpl.SpongeLogger;
import com.ericgrandt.totaleconomy.config.PluginConfig;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.listeners.JobListener;
import com.ericgrandt.totaleconomy.listeners.PlayerListener;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import com.google.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.parameter.Parameter;
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
    private final SpongeWrapper spongeWrapper;

    private PluginConfig config;
    private CurrencyDto defaultCurrency;
    private EconomyImpl economyImpl;
    private CommonEconomy economy;
    private JobService jobService;

    @Inject
    private TotalEconomy(
        final PluginContainer container,
        final Logger logger,
        final @DefaultConfig(sharedRoot = false) ConfigurationReference<CommentedConfigurationNode> configurationReference
    ) {
        this.container = container;
        this.logger = logger;
        this.configurationReference = configurationReference;
        this.spongeWrapper = new SpongeWrapper();
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
        JobData jobData = new JobData(new SpongeLogger(logger), database);

        economy = new CommonEconomy(new SpongeLogger(logger), accountData, balanceData, currencyData);
        economyImpl = new EconomyImpl(spongeWrapper, defaultCurrency, economy);
        jobService = new JobService(jobData);

        registerListeners();
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        Command.Parameterized balanceCommand = Command.builder()
            .executor(new BalanceCommandExecutor(economy, defaultCurrency, spongeWrapper))
            .build();
        event.register(container, balanceCommand, "balance");

        Command.Parameterized payCommand = Command.builder()
            .executor(new PayCommandExecutor(economy, defaultCurrency, spongeWrapper))
            .addParameter(Parameter.player().key("toPlayer").build())
            .addParameter(Parameter.doubleNumber().key("amount").build())
            .build();
        event.register(container, payCommand, "pay");

        if (config.getFeatures().get("jobs")) {
            JobCommandExecutor jobCommandExecutor = new JobCommandExecutor(
                new JobCommand(jobService),
                spongeWrapper
            );

            Command.Parameterized jobCommand = Command.builder()
                .executor(jobCommandExecutor)
                .build();
            event.register(container, jobCommand, "job");
        }
    }

    @Listener
    public void registerEconomyService(ProvideServiceEvent.EngineScoped<EconomyImpl> event) {
        event.suggest(() -> economyImpl);
    }

    private void registerListeners() {
        Sponge.eventManager().registerListeners(
            container,
            new PlayerListener(new CommonPlayerListener(economy, jobService))
        );

        if (config.getFeatures().get("jobs")) {
            Sponge.eventManager().registerListeners(
                container,
                new JobListener(
                    spongeWrapper,
                    new CommonJobListener(economy, jobService, defaultCurrency.id())
                )
            );
        }
    }
}
