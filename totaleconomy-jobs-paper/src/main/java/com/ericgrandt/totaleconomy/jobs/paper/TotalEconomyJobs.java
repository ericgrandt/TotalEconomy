package com.ericgrandt.totaleconomy.jobs.paper;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.api.infra.DataSourceProvider;
import com.ericgrandt.totaleconomy.api.service.EconomyService;
import com.ericgrandt.totaleconomy.jobs.data.DatabaseSetup;
import com.ericgrandt.totaleconomy.jobs.job.RewardCalculator;
import com.ericgrandt.totaleconomy.jobs.paper.config.ConfigLoader;
import com.ericgrandt.totaleconomy.jobs.paper.listener.BlockBreakListener;
import com.ericgrandt.totaleconomy.jobs.paper.util.PaperAsyncTaskRunner;
import com.ericgrandt.totaleconomy.jobs.service.BlockBreakService;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class TotalEconomyJobs extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy Jobs");
    private final AsyncTaskRunner taskRunner = new PaperAsyncTaskRunner(this);

    @Override
    public void onEnable() {
        var dataSourceProvider = getServer().getServicesManager().load(DataSourceProvider.class);
        var economyService = getServer().getServicesManager().load(EconomyService.class);
        if (dataSourceProvider == null || economyService == null) {
            logger.error("TotalEconomy services not found. Ensure you have TotalEconomy installed.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();

        var configLoader = new ConfigLoader();
        var config = configLoader.from(getConfig());
        var dataSource = dataSourceProvider.getDataSource();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseSetup.init(conn);
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        var rewardsCalculator = new RewardCalculator(config);
        var blockBreakService = new BlockBreakService(rewardsCalculator);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(rewardsCalculator), this);
    }
}
