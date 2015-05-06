package com.erigitic.jobs;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;

import java.io.File;
import java.io.IOException;

/**
 * Created by Erigitic on 5/5/2015.
 */
public class TEJobs {
    //TODO: Configuration file to allow people to edit jobs.
    //TODO: Add nodes to accounts config that relate to jobs

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private ConfigurationNode accountsConfig;
    private Logger logger;

    private File jobsFile; //Will contain all the jobs with salaries, tasks(Pay, xp reward), jobName, and etc.
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private ConfigurationNode jobsConfig;

    public TEJobs(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();
        accountsConfig = accountManager.getAccountConfig();
        logger = totalEconomy.getLogger();

        jobsFile = new File("config/TotalEconomy/jobs.conf");
        configManager = HoconConfigurationLoader.builder().setFile(jobsFile).build();

        try {
            jobsConfig = configManager.load();
        } catch (IOException e) {
            logger.warn("Could not load jobs config!");
        }
    }

    public void setupConfig() {
        try {
            if (!jobsFile.exists()) {
                jobsFile.createNewFile();
            }
        } catch (IOException e) {
            logger.warn("Could not create jobs config file!");
        }
    }

    /**
     * Pays the player their salary on a timed basis. This time can be changed in config(TODO).
     *
     * @param jobName (MAY NOT BE NEEDED) name of the job
     */
    public void paySalary(String jobName) {

    }

    /**
     * Get the player's current job
     *
     * @param player
     * @return String the job the player currently has
     */
    public String getJob(Player player) {
        String jobName = "";

        return jobName;
    }

    /**
     * Set the player's job.
     *
     * @param player Player object
     * @param jobName name of the job
     */
    public void setJob(Player player, String jobName) {

    }
}
