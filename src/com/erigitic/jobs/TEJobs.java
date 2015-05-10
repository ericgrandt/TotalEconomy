package com.erigitic.jobs;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerBreakBlockEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Erigitic on 5/5/2015.
 */
public class TEJobs {
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private ConfigurationNode accountConfig;
    private Logger logger;

    private File jobsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode jobsConfig;

    /**
     * Constructor
     *
     * @param totalEconomy object representing this plugin
     */
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

    /**
     * Setup the jobs config
     */
    public void setupConfig() {
        try {
            String[][] minerBreakables = {{"coal_ore", "5", "0.25"}, {"iron_ore", "10", "0.50"}, {"lapis_ore", "20", "4.00"}, {"gold_ore", "40", "5.00"}, {"diamond_ore", "100", "25.00"},
                    {"redstone_ore", "25", "2.00"}, {"emerald_ore", "50", "12.50"}, {"quartz_ore", "5", "0.15"}};

            if (!jobsFile.exists()) {
                jobsFile.createNewFile();

                jobsConfig.getNode("jobs").setValue("Miner, Lumberjack");
                for (int i = 0; i < minerBreakables.length; i++) {
                    jobsConfig.getNode("Miner", "break", minerBreakables[i][0], "expreward").setValue(minerBreakables[i][1]);
                    jobsConfig.getNode("Miner", "break", minerBreakables[i][0], "pay").setValue(minerBreakables[i][2]);
                }

                loader.save(jobsConfig);
            }
        } catch (IOException e) {
            logger.warn("Could not create jobs config file!");
        }
    }

    /**
     * Setup the job ranks
     */
    //TODO: Might remove
    private void setupRanks() {
        jobsConfig.getNode("ranks", "novice").setValue("1");
        jobsConfig.getNode("ranks", "advanced").setValue("25");
        jobsConfig.getNode("ranks", "proficient").setValue("50");
        jobsConfig.getNode("ranks", "expert").setValue("150");
        jobsConfig.getNode("ranks", "master").setValue("300");
    }


    /**
     * Add exp to player's current job
     *
     * @param player player object
     * @param expAmount amount of exp to be gained
     */
    public void addExp(Player player, int expAmount) {
        String jobName = getPlayerJob(player);
        UUID playerUUID = player.getUniqueId();
        int curExp = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").getInt();

        accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").setValue(curExp + expAmount);

        try {
            accountManager.getConfigManager().save(accountConfig);
        } catch (IOException e) {
            logger.warn("Problem saving account config!");
        }

        player.sendMessage(Texts.of(TextColors.GRAY, "You have gained ", TextColors.GOLD, expAmount, TextColors.GRAY, " exp in the ", TextColors.GOLD, jobName, TextColors.GRAY, " job."));
    }

    /**
     * Get the player's current job
     *
     * @param player
     * @return String the job the player currently has
     */
    public String getPlayerJob(Player player) {
        return accountConfig.getNode(player.getUniqueId().toString(), "job").getString();
    }

    /**
     * Set the player's job.
     *
     * @param player Player object
     * @param jobName name of the job
     */
    public void setJob(Player player, String jobName) {
        UUID playerUUID = player.getUniqueId();

        if (jobExists(jobName)) {
            accountConfig.getNode(playerUUID.toString(), "job").setValue(jobName);

            if (accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Level").getValue() == null) {
                accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Level").setValue(1);
            }

            if (accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").getValue() == null) {
                accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").setValue(0);
            }

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
     * Get the players exp for the passed in job.
     *
     * @param jobName the name of the job
     * @param player the player object
     * @return int the job xp
     */
    public int getJobExp(String jobName, Player player) {
        return accountConfig.getNode(player.getUniqueId().toString(), "jobstats", jobName + "Exp").getInt();
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

    /**
     * Get items and exp rewards for items relating to the job in config
     *
     * @param jobName name of job
     * @return String[][] item and exp reward
     */
    //TODO: IMPLEMENT THIS SO PEOPLE CAN MAKE THEIR OWN JOBS AND MODIFICATIONS
    public String[][] getExpRewards(String jobName) {

        return null;
    }

    @Subscribe
    public void onPlayerBlockBreak(PlayerBreakBlockEvent event) {
        Player player = event.getPlayer();
        String playerJob = getPlayerJob(player);
        String blockName = event.getBlock().getType().getName().split(":")[1];

        //TODO: Implement better
        if (jobsConfig.getNode(playerJob).getValue() != null && jobsConfig.getNode("Miner", "break", blockName).getValue() != null) {
            int expAmount = jobsConfig.getNode("Miner", "break", blockName, "expreward").getInt();
            double payAmount = jobsConfig.getNode("Miner", "break", blockName, "pay").getDouble();

            addExp(player, expAmount);
            accountManager.addToBalance(player, new BigDecimal(payAmount));
        }
    }
}