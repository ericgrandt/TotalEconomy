package com.ericgrandt.totaleconomy.jobs.paper;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.api.infra.DataSourceProvider;
import com.ericgrandt.totaleconomy.api.model.Currency;
import com.ericgrandt.totaleconomy.api.service.EconomyService;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.data.ActiveJobData;
import com.ericgrandt.totaleconomy.jobs.data.DatabaseSetup;
import com.ericgrandt.totaleconomy.jobs.data.JobExperienceData;
import com.ericgrandt.totaleconomy.jobs.job.JobCalculator;
import com.ericgrandt.totaleconomy.jobs.paper.command.JobCommand;
import com.ericgrandt.totaleconomy.jobs.paper.config.ConfigLoader;
import com.ericgrandt.totaleconomy.jobs.paper.listener.BlockBreakListener;
import com.ericgrandt.totaleconomy.jobs.paper.mapper.ExceptionMapper;
import com.ericgrandt.totaleconomy.jobs.paper.util.PaperAsyncTaskRunner;
import com.ericgrandt.totaleconomy.jobs.service.BlockBreakService;
import com.ericgrandt.totaleconomy.jobs.service.JobService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class TotalEconomyJobs extends JavaPlugin {
    private final Logger logger = LoggerFactory.getLogger("Total Economy Jobs");
    private final AsyncTaskRunner taskRunner = new PaperAsyncTaskRunner(this);

    private EconomyService<Currency> economyService;
    private Config config;
    private ExceptionMapper exceptionMapper;
    private JobService jobService;
    private BlockBreakService blockBreakService;

    @Override
    public void onEnable() {
        var dataSourceProvider = getServer().getServicesManager().load(DataSourceProvider.class);
        economyService = getServer().getServicesManager().load(EconomyService.class);
        if (dataSourceProvider == null || economyService == null) {
            logger.error("TotalEconomy services not found. Ensure you have TotalEconomy installed.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();

        var configLoader = new ConfigLoader();
        config = configLoader.from(getConfig());
        var dataSource = dataSourceProvider.getDataSource();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseSetup.init(conn);
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        var transactionUtil = new TransactionUtil(dataSource);
        var activeJobData = new ActiveJobData();
        var jobExperienceData = new JobExperienceData();
        var rewardsCalculator = new JobCalculator(config);

        exceptionMapper = new ExceptionMapper(logger);
        jobService = new JobService(transactionUtil, activeJobData, jobExperienceData);

        blockBreakService = new BlockBreakService(
            transactionUtil,
            jobExperienceData,
            economyService,
            rewardsCalculator,
            config
        );

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        var jobCommand = new JobCommand(this, taskRunner, exceptionMapper, config, jobService);

        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS, commands -> {
                    commands.registrar().register(jobCommand.build());
                }
        );
    }

    private void registerListeners() {
        getServer().getPluginManager()
                .registerEvents(new BlockBreakListener(taskRunner, exceptionMapper, blockBreakService, jobService), this);
    }
}
