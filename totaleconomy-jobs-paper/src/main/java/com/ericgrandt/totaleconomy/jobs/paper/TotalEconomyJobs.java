package com.ericgrandt.totaleconomy.jobs.paper;

import com.ericgrandt.totaleconomy.data.DataSourceProvider;
import com.ericgrandt.totaleconomy.jobs.paper.config.ConfigLoader;
import com.ericgrandt.totaleconomy.jobs.paper.util.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.jobs.paper.util.PaperAsyncTaskRunner;
import com.ericgrandt.totaleconomy.service.EconomyService;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotalEconomyJobs extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy Jobs");
    private final AsyncTaskRunner taskRunner = new PaperAsyncTaskRunner();

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

        var config = ConfigLoader.from(getConfig());
        var dataSource = dataSourceProvider.getDataSource();

        //try {
        //    database.initJobDatabase(dataSource.getConnection());
        //} catch (SQLException e) {
        //    logger.error("Error initializing database", e);
        //    getServer().getPluginManager().disablePlugin(this);
        //    return;
        //}

        //var transactionUtil = new TransactionUtil(dataSource);
    }
}
