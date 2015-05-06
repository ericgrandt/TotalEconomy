package com.erigitic.jobs;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;

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
    private ConfigurationNode accountConfig;
    private Logger logger;

    private File jobsFile; //Will contain all the jobs with salaries, tasks(Pay, xp reward), jobName, and etc.
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private ConfigurationNode jobsConfig;

    public TEJobs(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();
        accountConfig = accountManager.getAccountConfig();
        logger = totalEconomy.getLogger();

        jobsFile = new File(totalEconomy.getConfigDir(), "jobs.conf");
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

                jobsConfig.getNode("Miner", "salary").setValue("25");//600 seconds
                configManager.save(jobsConfig);
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
    public String getPlayerJob(Player player) {
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
        if (jobExists(jobName)) {
            accountConfig.getNode(player.getUniqueId().toString(), "job").setValue(jobName);

            try {
                accountManager.getConfigManager().save(accountConfig);
            } catch (IOException e) {
                logger.warn("Could not save account config while setting job!");
            }

            player.sendMessage(Texts.of("Your job has been changed to " + jobName));
        } else {
            player.sendMessage(Texts.of("That is not a job."));
        }
    }

    public String[] getJobList() {

        return null;
    }

    /**
     * Checks the jobs config for the jobName.
     *
     * @param jobName name of the job
     * @return boolean if the job exists or not
     */
    public boolean jobExists(String jobName) {
        if (jobsConfig.getNode(jobName).getValue() != null) {
            return true;
        }

        return false;
    }
}
