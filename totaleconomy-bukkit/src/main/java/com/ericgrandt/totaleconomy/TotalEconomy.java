package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.commands.BalanceCommand;
import com.ericgrandt.totaleconomy.commands.JobCommand;
import com.ericgrandt.totaleconomy.commands.PayCommand;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.config.PluginConfig;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.listeners.JobListener;
import com.ericgrandt.totaleconomy.listeners.PlayerListener;
import com.ericgrandt.totaleconomy.services.BalanceService;
import com.ericgrandt.totaleconomy.services.JobService;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TotalEconomy extends JavaPlugin implements Listener {
    private final FileConfiguration fileConfiguration = getConfig();
    private final PluginConfig config = new PluginConfig(fileConfiguration);
    private final Logger logger = Logger.getLogger("Minecraft");
    private final BukkitWrapper bukkitWrapper = new BukkitWrapper();

    private EconomyImpl economy;
    private JobService jobService;
    private BalanceService balanceService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Database database = new Database(
            config.getDatabaseUrl(),
            config.getDatabaseUser(),
            config.getDatabasePassword()
        );

        try {
            database.initDatabase();
        } catch (SQLException | IOException e) {
            logger.log(
                Level.SEVERE,
                "[Total Economy] Error calling initDatabase",
                e
            );
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CurrencyData currencyData = new CurrencyData(database);
        CurrencyDto defaultCurrency;

        try {
            defaultCurrency = currencyData.getDefaultCurrency();
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                "[Total Economy] Unable to load default currency",
                e
            );
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        AccountData accountData = new AccountData(database);
        BalanceData balanceData = new BalanceData(database);
        JobData jobData = new JobData(database);
        economy = new EconomyImpl(logger, this.isEnabled(), defaultCurrency, accountData, balanceData);
        jobService = new JobService(logger, jobData);
        balanceService = new BalanceService(balanceData);

        getServer().getServicesManager().register(
            Economy.class,
            economy,
            this,
            ServicePriority.Normal
        );

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("balance")).setExecutor(new BalanceCommand(economy));
        Objects.requireNonNull(this.getCommand("pay")).setExecutor(
            new PayCommand(logger, bukkitWrapper, economy, balanceService)
        );

        if (config.getFeatures().get("jobs")) {
            JobCommand jobCommand = new JobCommand(logger, jobService);

            Objects.requireNonNull(this.getCommand("job")).setExecutor(jobCommand);
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(economy, jobService, this), this);

        if (config.getFeatures().get("jobs")) {
            getServer().getPluginManager().registerEvents(new JobListener(economy, jobService), this);
        }
    }
}
