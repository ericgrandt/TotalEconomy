package com.ericgrandt.totaleconomy.paper;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.api.infra.DataSourceProvider;
import com.ericgrandt.totaleconomy.api.service.EconomyService;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.model.TECurrency;
import com.ericgrandt.totaleconomy.paper.command.BalanceCommand;
import com.ericgrandt.totaleconomy.paper.command.PayCommand;
import com.ericgrandt.totaleconomy.paper.config.ConfigLoader;
import com.ericgrandt.totaleconomy.paper.impl.VaultImpl;
import com.ericgrandt.totaleconomy.paper.listener.JoinListener;
import com.ericgrandt.totaleconomy.paper.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.paper.util.PaperAsyncTaskRunner;
import com.ericgrandt.totaleconomy.service.CacheService;
import com.ericgrandt.totaleconomy.service.TEEconomyService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class TotalEconomy extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy");
    private final AsyncTaskRunner taskRunner = new PaperAsyncTaskRunner(this);

    private CommandExceptionMapper exceptionMapper;
    private EconomyService<TECurrency> economyService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        var config = ConfigLoader.from(getConfig());
        var database = new Database(
            config.database().url(),
            config.database().user(),
            config.database().password()
        );

        try {
            database.initDatabase(config);
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        var transactionUtil = new TransactionUtil(database.getDataSource());
        var accountData = new AccountData();
        var currencyData = new CurrencyData();
        var cacheService = new CacheService(transactionUtil, currencyData);
        exceptionMapper = new CommandExceptionMapper(logger);
        economyService = new TEEconomyService(transactionUtil, cacheService, currencyData, accountData);

        cacheService.initCache();

        getServer().getServicesManager().register(
            EconomyService.class,
            economyService,
            this,
            ServicePriority.Normal
        );
        getServer().getServicesManager().register(
            DataSourceProvider.class,
            database,
            this,
            ServicePriority.Normal
        );

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            getServer().getServicesManager().register(
                Economy.class,
                new VaultImpl(logger, economyService),
                this,
                ServicePriority.Normal
            );
        }

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        var balanceCommand = new BalanceCommand(this, taskRunner, exceptionMapper, economyService);
        var payCommand = new PayCommand(this, taskRunner, exceptionMapper, economyService);

        this.getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS, commands -> {
                commands.registrar().register(balanceCommand.build());
                commands.registrar().register(payCommand.build());
            }
        );
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
            new JoinListener(
                taskRunner,
                logger,
                economyService
            ), this
        );
    }
}
