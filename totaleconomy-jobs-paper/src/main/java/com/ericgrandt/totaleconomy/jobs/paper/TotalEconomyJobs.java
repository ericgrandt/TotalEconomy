package com.ericgrandt.totaleconomy.jobs.paper;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.api.infra.DataSourceProvider;
import com.ericgrandt.totaleconomy.api.infra.TransactionService;
import com.ericgrandt.totaleconomy.api.service.EconomyService;
import com.ericgrandt.totaleconomy.jobs.paper.config.ConfigLoader;
import com.ericgrandt.totaleconomy.jobs.paper.util.PaperAsyncTaskRunner;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotalEconomyJobs extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy Jobs");
    private final AsyncTaskRunner taskRunner = new PaperAsyncTaskRunner(this);

    @Override
    public void onEnable() {
        var dataSourceProvider = getServer().getServicesManager().load(DataSourceProvider.class);
        var economyService = getServer().getServicesManager().load(EconomyService.class);
        var transactionService = getServer().getServicesManager().load(TransactionService.class);
        if (dataSourceProvider == null || economyService == null) {
            logger.error("TotalEconomy services not found. Ensure you have TotalEconomy installed.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();

        var config = ConfigLoader.from(getConfig());
        var dataSource = dataSourceProvider.getDataSource();
    }
}
