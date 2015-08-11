package com.erigitic.jobs;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.*;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

            String[][] lumberBreakables = {{"log", "10", "1"}, {"leaves", "1", ".01"}};
            String[][] lumberPlaceables = {{"sapling", "1", "0.10"}};

            if (!jobsFile.exists()) {
                jobsFile.createNewFile();

                jobsConfig.getNode("preventJobFarming").setValue(false);
                jobsConfig.getNode("jobs").setValue("Miner,Lumberjack");

                for (int i = 0; i < minerBreakables.length; i++) {
                    jobsConfig.getNode("Miner", "break", minerBreakables[i][0], "expreward").setValue(minerBreakables[i][1]);
                    jobsConfig.getNode("Miner", "break", minerBreakables[i][0], "pay").setValue(minerBreakables[i][2]);
                }

                for (int i = 0; i < lumberBreakables.length; i++) {
                    jobsConfig.getNode("Lumberjack", "break", lumberBreakables[i][0], "expreward").setValue(lumberBreakables[i][1]);
                    jobsConfig.getNode("Lumberjack", "break", lumberBreakables[i][0], "pay").setValue(lumberBreakables[i][2]);
                }
                for (int i = 0; i < lumberPlaceables.length; i++) {
                    jobsConfig.getNode("Lumberjack", "place", lumberPlaceables[i][0], "expreward").setValue(lumberPlaceables[i][1]);
                    jobsConfig.getNode("Lumberjack", "place", lumberPlaceables[i][0], "pay").setValue(lumberPlaceables[i][2]);
                }

                loader.save(jobsConfig);
            }
        } catch (IOException e) {
            logger.warn("Could not create jobs config file!");
        }
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

        if (accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean() == true)
            player.sendMessage(Texts.of(TextColors.GRAY, "You have gained ", TextColors.GOLD, expAmount, TextColors.GRAY, " exp in the ", TextColors.GOLD, jobName, TextColors.GRAY, " job."));
    }

    /**
     * Checks if the player has enough exp to level up. If they do they will gain a level and their current exp will be
     * reset.
     *
     * @param player player object
     */
    public void checkForLevel(Player player) {
        UUID playerUUID = player.getUniqueId();
        String jobName = getPlayerJob(player);
        int playerLevel = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Level").getInt();
        int playerCurExp = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").getInt();
        int expToLevel = getExpToLevel(player);

        if (playerCurExp >= expToLevel) {
            accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Level").setValue(playerLevel + 1);
            accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").setValue(playerCurExp - expToLevel);
            player.sendMessage(Texts.of(TextColors.GRAY, "Congratulations, you are now a level ", TextColors.GOLD, playerLevel + 1, " ", jobName));
            //TODO: Some effect such as fireworks on level up?
        }
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
     * Get the player's current job
     *
     * @param player
     * @return String the job the player currently has
     */
    public String getPlayerJob(Player player) {
        return accountConfig.getNode(player.getUniqueId().toString(), "job").getString();
    }

    /**
     * Get the players exp for the passed in job.
     *
     * @param jobName the name of the job
     * @param player the player object
     * @return int the job exp
     */
    public int getJobExp(String jobName, Player player) {
        return accountConfig.getNode(player.getUniqueId().toString(), "jobstats", jobName + "Exp").getInt();
    }

    /**
     * Get the players level for the passed in job
     *
     * @param jobName the name of the job
     * @param player the player object
     * @return int the job level
     */
    public int getJobLevel(String jobName, Player player) {
        return accountConfig.getNode(player.getUniqueId().toString(), "jobstats", jobName + "Level").getInt();
    }

    /**
     * Get the exp required to level.
     *
     * @param player player object
     * @return int the amount of exp needed to level
     */
    public int getExpToLevel(Player player) {
        UUID playerUUID = player.getUniqueId();
        String jobName = getPlayerJob(player);
        int playerLevel = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Level").getInt();

        return playerLevel * 100;
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
     * Used for the break option in jobs. Will check if the job has the break node and if it does it will check if the
     * block that was broken is present in the config of the player's job. If it is, it will grab the job exp reward as
     * well as the pay.
     *
     * @param event PlayerBlockBreakEvent
     */
    @Subscribe
    public void onPlayerBlockBreak(PlayerBreakBlockEvent event) {
        Player player = event.getUser();
        UUID playerUUID = player.getUniqueId();
        String playerJob = getPlayerJob(player);
        String blockName = event.getBlock().getType().getName().split(":")[1];//Will turn the block name from 'minecraft:block' to 'block'.

        //Checks if the users current job has the break node.
        boolean hasBreak = (jobsConfig.getNode(playerJob, "break").getValue() != null);
        boolean preventFarming = jobsConfig.getNode("preventJobFarming").getBoolean();

        if (jobsConfig.getNode(playerJob).getValue() != null) {
            if (hasBreak && jobsConfig.getNode(playerJob, "break", blockName).getValue() != null) {
                if (preventFarming)
                    event.getLocation().setBlockType(BlockTypes.AIR);

                int expAmount = jobsConfig.getNode(playerJob, "break", blockName, "expreward").getInt();
                BigDecimal payAmount = new BigDecimal(jobsConfig.getNode(playerJob, "break", blockName, "pay").getString()).setScale(2, BigDecimal.ROUND_DOWN);
                boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                addExp(player, expAmount);
                checkForLevel(player);
                accountManager.addToBalance(player.getUniqueId(), payAmount, notify);
            }
        }
    }

    @Subscribe
    public void onPlayerPlaceBlock(PlayerPlaceBlockEvent event) {
        Player player = event.getUser();
        UUID playerUUID = player.getUniqueId();
        String playerJob = getPlayerJob(player);
        String blockName = event.getBlock().getType().getName().split(":")[1];//Will turn the block name from 'minecraft:block' to 'block'.
        DataQuery pQuery = new DataQuery("player");

        //Checks if the users current job has the place node.
        boolean hasPlace = (jobsConfig.getNode(playerJob, "place").getValue() != null);

        if (jobsConfig.getNode(playerJob).getValue() != null) {
            if (hasPlace && jobsConfig.getNode(playerJob, "place", blockName).getValue() != null) {
                int expAmount = jobsConfig.getNode(playerJob, "place", blockName, "expreward").getInt();
                BigDecimal payAmount = new BigDecimal(jobsConfig.getNode(playerJob, "place", blockName, "pay").getString()).setScale(2, BigDecimal.ROUND_DOWN);
                boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                addExp(player, expAmount);
                checkForLevel(player);
                accountManager.addToBalance(player.getUniqueId(), payAmount, notify);
            }
        }
    }

    @Subscribe
    public void onPlayerAttack(PlayerInteractEntityEvent event) {
        Player player = event.getUser();

        player.sendMessage(Texts.of("Attacked"));
    }

    //TODO: Complete when fully implemented
//    @Subscribe
//    public void onPlayerCreate(CraftItemEvent event) {
//        CraftingOutput craftItem = event.getInventory().getResult();
//
//        if (event.getViewer().getType() instanceof Player) {
//            Player player = (Player) event.getViewer();
//        }
//    }

    //TODO: Complete when fully implemented
//    @Subscribe
//    public void onPlayerSmelt(FurnaceSmeltItemEvent event) {
//        String resultName = event.getSourceItem().getItem().getName();
//
//        logger.info(resultName);
//    }
}