package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.command.BalanceCommand;
import com.ericgrandt.totaleconomy.command.PayCommand;
import com.ericgrandt.totaleconomy.config.ConfigLoader;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.listener.JoinListener;
import com.ericgrandt.totaleconomy.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.TEEconomyService;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.util.PaperAsyncTaskRunner;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class TotalEconomy extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy");
    private final AsyncTaskRunner taskRunner = new PaperAsyncTaskRunner();

    private CommandExceptionMapper exceptionMapper;
    private TEEconomyService economyService;

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
        exceptionMapper = new CommandExceptionMapper(logger);
        economyService = new TEEconomyService(transactionUtil, currencyData, accountData);

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
                this,
                taskRunner,
                logger,
                economyService
            ), this
        );
    }
}
