package com.erigitic.jobs;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.util.MapFactories;
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

    private File jobsFile; //Will contain all the jobs with salaries, jobName, and etc.
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode jobsConfig;

    public TEJobs(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();
        accountConfig = accountManager.getAccountConfig();
        logger = totalEconomy.getLogger();

        jobsFile = new File(totalEconomy.getConfigDir(), "jobs.conf");
        loader = HoconConfigurationLoader.builder().setFile(jobsFile).build();

        try {
            jobsConfig = loader.load();
        } catch (IOException e) {
            logger.warn("Could not load jobs config!");
        }
    }

    public void setupConfig() {
        try {
            if (!jobsFile.exists()) {
                jobsFile.createNewFile();

                jobsConfig.getNode("jobs").setValue("Miner, Lumberjack");
                jobsConfig.getNode("Miner", "basepay").setValue("1");
                jobsConfig.getNode("Miner", "experience", "coal_ore").setValue("4");
                jobsConfig.getNode("Lumberjack", "basepay").setValue("1");
                jobsConfig.getNode("Lumberjack", "experience", "item1").setValue("4");

                setupRanks();

                loader.save(jobsConfig);
            }
        } catch (IOException e) {
            logger.warn("Could not create jobs config file!");
        }
    }

    /**
     * Setup the job ranks
     */
    private void setupRanks() {
        jobsConfig.getNode("ranks", "novice").setValue("1");
        jobsConfig.getNode("ranks", "advanced").setValue("25");
        jobsConfig.getNode("ranks", "proficient").setValue("50");
        jobsConfig.getNode("ranks", "expert").setValue("150");
        jobsConfig.getNode("ranks", "master").setValue("300");
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

    public String[] getTasks() {

        return null;
    }
}