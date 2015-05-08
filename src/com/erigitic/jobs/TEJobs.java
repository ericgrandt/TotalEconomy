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
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;

/**
 * Created by Erigitic on 5/5/2015.
 */
public class TEJobs {
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

                jobsConfig.getNode("jobs").setValue("Miner, Lumberjack");
                jobsConfig.getNode("Miner", "salary").setValue("25");
                jobsConfig.getNode("Lumberjack", "salary").setValue("25");
                configManager.save(jobsConfig);
            }

            if (jobsConfig.getNode("jobs").getValue() == null) {
                jobsConfig.getNode("jobs").setValue("Miner, Lumberjack");
            }

            if (jobsConfig.getNode("Miner", "salary").getValue() == null) {
                jobsConfig.getNode("Miner", "salary").setValue("25");
            }

            if (jobsConfig.getNode("Lumberjack", "salary").getValue() == null) {
                jobsConfig.getNode("Lumberjack", "salary").setValue("25");
            }
        } catch (IOException e) {
            logger.warn("Could not create jobs config file!");
        }
    }

    /**
     * Pays the player their salary on a timed basis. This time can be changed in config(TODO).
     *
     * NOTE: Not sure if I want to have a salary or not. Will decide later. Currently this function will be empty.
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
        return accountConfig.getNode(player.getUniqueId().toString(), "job").getValue().toString();
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

            player.sendMessage(Texts.of(TextColors.GRAY, "Your job has been changed to ", TextColors.GOLD, jobName));
        } else {
            player.sendMessage(Texts.of(TextColors.RED, "That is not a job."));
        }
    }

    /**
     * Gets a list of all of the jobs currently in the jobs config.
     *
     * @return String list of jobs
     */
    public String getJobList() {
        return jobsConfig.getNode("jobs").getString();
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
