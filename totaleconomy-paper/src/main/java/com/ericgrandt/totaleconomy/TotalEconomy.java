package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.command.BalanceCommand;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.listener.JoinListener;
import com.ericgrandt.totaleconomy.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.EconomyService;
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
    private EconomyService economyService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        var database = new Database(
            getConfig().getString("database.url"),
            getConfig().getString("database.user"),
            getConfig().getString("database.password")
        );

        try {
            database.initDatabase();
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        var transactionUtil = new TransactionUtil(database.getDataSource());
        var accountData = new AccountData();
        var currencyData = new CurrencyData();
        exceptionMapper = new CommandExceptionMapper(logger);
        economyService = new EconomyService(transactionUtil, currencyData, accountData);

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        var balanceCommand = new BalanceCommand(this, taskRunner, exceptionMapper, economyService);
        this.getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS, commands -> {
                commands.registrar().register(balanceCommand.build());
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
