package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.command.BalanceCommand;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.mapper.ExceptionMapper;
import com.ericgrandt.totaleconomy.service.EconomyService;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.util.PaperAsyncTaskRunner;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

public class TotalEconomy extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy");
    private final AsyncTaskRunner taskRunner = new PaperAsyncTaskRunner();

    private ExceptionMapper exceptionMapper;
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
        exceptionMapper = new ExceptionMapper(logger);
        economyService = new EconomyService(transactionUtil, currencyData, accountData);

        registerCommands();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("balance")).setExecutor(new BalanceCommand(
            this,
            taskRunner,
            exceptionMapper,
            economyService
        ));
    }
}
