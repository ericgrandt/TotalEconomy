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
import com.ericgrandt.totaleconomy.commonimpl.BukkitLogger;
import com.ericgrandt.totaleconomy.config.PluginConfig;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.listeners.JobListener;
import com.ericgrandt.totaleconomy.listeners.PlayerListener;
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

    private CommonEconomy economy;
    private JobService jobService;
    private CurrencyDto defaultCurrency;

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
        JobData jobData = new JobData(new BukkitLogger(logger), database);
        economy = new CommonEconomy(new BukkitLogger(logger), accountData, balanceData, currencyData);
        EconomyImpl economyImpl = new EconomyImpl(true, defaultCurrency, economy);
        jobService = new JobService(jobData);

        getServer().getServicesManager().register(
            Economy.class,
            economyImpl,
            this,
            ServicePriority.Normal
        );

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("balance")).setExecutor(
            new BalanceCommandExecutor(economy, defaultCurrency)
        );
        Objects.requireNonNull(this.getCommand("pay")).setExecutor(
            new PayCommandExecutor(economy, defaultCurrency, bukkitWrapper)
        );

        if (config.getFeatures().get("jobs")) {
            JobCommandExecutor jobCommandExecutor = new JobCommandExecutor(
                new JobCommand(jobService)
            );

            Objects.requireNonNull(this.getCommand("job")).setExecutor(jobCommandExecutor);
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
            new PlayerListener(new CommonPlayerListener(economy, jobService)),
            this
        );

        if (config.getFeatures().get("jobs")) {
            CommonJobListener commonJobListener = new CommonJobListener(economy, jobService, defaultCurrency.id());
            getServer().getPluginManager().registerEvents(
                new JobListener(commonJobListener),
                this
            );
        }
    }
}
