package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.economy.EconomyProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class TotalEconomy extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy");
    private EconomyProvider economy;

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
        economy = new EconomyProvider(logger, transactionUtil, currencyData, accountData);

        registerCommands();
    }

    private void registerCommands() {
        //getCommand("balance").setExecutor(BalanceCommandExecutor(pluginScope, BalanceCommand(economy)))
    }
}
