/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.jobs;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.jobs.jobs.*;
import com.erigitic.jobs.jobsets.*;
import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.SQLHandler;
import com.erigitic.sql.SQLQuery;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.block.trait.IntegerTrait;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.manipulator.mutable.item.FishData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.service.economy.Currency;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TEJobManager {

    // We only need one instance of this
    public static final JobSet[] defaultJobSets = {
            new FishermanJobSet(),
            new LumberjackJobSet(),
            new MinerJobSet(),
            new WarriorJobSet(),
            new FarmerJobSet()
    };

    // We only need one instance of this
    private final Job[] defaultJobsArr = {
            new UnemployedJob(),
            new FishermanJob(),
            new LumberjackJob(),
            new MinerJob(),
            new WarriorJob(),
            new FarmerJob()
    };

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private ConfigurationNode accountConfig;
    private Logger logger;

    private File jobSetsFile;
    private ConfigurationLoader<CommentedConfigurationNode> jobSetsLoader;
    private ConfigurationNode jobSetsConfig;
    private Map<String, TEJobSet> jobSets;

    private File jobsFile;
    private ConfigurationLoader<CommentedConfigurationNode> jobsLoader;
    private ConfigurationNode jobsConfig;
    private Map<String, TEJob> jobsMap;

    private boolean databaseActive;
    private SQLHandler sqlHandler;

    public TEJobManager(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();
        accountConfig = accountManager.getAccountConfig();
        logger = totalEconomy.getLogger();
        databaseActive = totalEconomy.isDatabaseActive();

        if (databaseActive)
            sqlHandler = totalEconomy.getSqlHandler();

        setupConfig();

        if (totalEconomy.isLoadSalary())
            startSalaryTask();
    }

    /**
     * Start the timer that pays out the salary to each player after a specified time in seconds
     */
    private void startSalaryTask() {
        Scheduler scheduler = totalEconomy.getGame().getScheduler();
        Task.Builder payTask = scheduler.createTaskBuilder();

        payTask.execute(() -> {
                for (Player player : totalEconomy.getServer().getOnlinePlayers()) {
                    Optional<TEJob> optJob = getJob(getPlayerJob(player), true);

                    if (!optJob.isPresent()) {
                        player.sendMessage(Text.of(TextColors.RED, "[TE] Cannot pay your salary! Contact your administrator!"));

                        return;
                    }

                    if (optJob.get().salaryEnabled()) {
                        BigDecimal salary = optJob.get().getSalary();
                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                        TransactionResult result = playerAccount.deposit(totalEconomy.getDefaultCurrency(), salary, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

                        if (result.getResult() == ResultType.SUCCESS) {
                            player.sendMessage(Text.of(TextColors.GRAY, "Your salary of ", TextColors.GOLD, totalEconomy.getDefaultCurrency().format(salary), TextColors.GRAY, " has just been paid."));
                        } else {
                            player.sendMessage(Text.of(TextColors.RED, "[TE] Failed to pay your salary! You may want to contact your admin - TransactionResult: ", result.getResult().toString()));
                        }
                    }
                }
        }).delay(jobsConfig.getNode("salarydelay").getInt(), TimeUnit.SECONDS).interval(jobsConfig.getNode("salarydelay").getInt(), TimeUnit.SECONDS).name("Pay Day").submit(totalEconomy);
    }

    /**
     * Setup the jobs config
     */
    public void setupConfig() {
        jobSetsFile = new File(totalEconomy.getConfigDir(), "jobSets.conf");
        jobSetsLoader = HoconConfigurationLoader.builder().setFile(jobSetsFile).build();
        jobSets = new HashMap();
        reloadJobSetConfig();

        jobsFile = new File(totalEconomy.getConfigDir(), "jobs.conf");
        jobsLoader = HoconConfigurationLoader.builder().setFile(jobsFile).build();
        jobsMap = new HashMap();
        reloadJobsConfig();
    }

    /**
     * Reload the jobSet config
     */
    public boolean reloadJobSetConfig() {
        try {
            jobSetsConfig = jobSetsLoader.load();
            ConfigurationNode sets = jobSetsConfig.getNode("sets");

            if (!jobSetsFile.exists()) {
                for (JobSet s : defaultJobSets) {
                    s.populateNode(sets);
                }

                jobSetsLoader.save(jobSetsConfig);
            }

            sets.getChildrenMap().forEach((setName, setNode) -> {
                if (setNode != null) {
                    TEJobSet jobSet = new TEJobSet(setNode);

                    jobSets.put((String) setName, jobSet);
                }
            });

            return true;
        } catch (IOException e) {
            logger.warn("[TE] An error occurred while creating/loading the jobSets configuration file!");

            return false;
        }
    }

    /**
     * Reloads the job configuration file. Can be used for initial creation of the configuration file
     * or for simply reloading it.
     *
     * @return boolean Was the reload successful?
     */
    public boolean reloadJobsConfig() {
        try {
            jobsConfig = jobsLoader.load();
            ConfigurationNode jobsNode = jobsConfig.getNode("jobs");

            if (!jobsFile.exists()) {
                for (Job j : defaultJobsArr) {
                    j.populateNode(jobsNode);
                }

                jobsConfig.getNode("salarydelay").setValue(300);

                jobsLoader.save(jobsConfig);
            }

            // Loop through each job node in the configuration file, create a TEJob object from it, and store in a HashMap
            jobsNode.getChildrenMap().forEach((k, jobNode) -> {
                if (jobNode != null) {
                    TEJob job = new TEJob(jobNode);

                    if (job.isValid()) {
                        jobsMap.put(job.getName(), job);
                    }
                }
            });

            return true;
        } catch (IOException e) {
            logger.warn("[TE] An error occurred while creating/loading the jobs configuration file!");

            return false;
        }
    }

    /**
     * Reload all job configs (jobs + sets)
     */
    public boolean reloadJobsAndSets() {
        return reloadJobsConfig() && reloadJobSetConfig();
    }

    /**
     * Add exp to player's current job
     *
     * @param player The player to give experience to
     * @param expAmount The amount of experience to add
     */
    public void addExp(Player player, int expAmount) {
        String jobName = getPlayerJob(player);
        UUID playerUUID = player.getUniqueId();
        boolean jobNotifications = accountManager.getJobNotificationState(player);

        if (databaseActive) {
            int newExp = getJobExp(jobName, player) + expAmount;

            SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                    .update("totaleconomy.experience")
                    .set(jobName)
                    .equals(String.valueOf(newExp))
                    .where("uid")
                    .equals(playerUUID.toString())
                    .build();

            if (sqlQuery.getRowsAffected() > 0) {
                if (jobNotifications) {
                    player.sendMessage(Text.of(TextColors.GRAY, "You have gained ", TextColors.GOLD, expAmount, TextColors.GRAY, " exp in the ", TextColors.GOLD, jobName, TextColors.GRAY, " job."));
                }
            } else {
                logger.warn("[TE] An error occurred while updating job experience in the database!");
                player.sendMessage(Text.of(TextColors.RED, "[TE] Error adding experience! Consult an administrator!"));
            }
        } else {
            int curExp = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").getInt();

            accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").setValue(curExp + expAmount);

            if (jobNotifications)
                player.sendMessage(Text.of(TextColors.GRAY, "You have gained ", TextColors.GOLD, expAmount, TextColors.GRAY, " exp in the ", TextColors.GOLD, jobName, TextColors.GRAY, " job."));

            try {
                accountManager.getConfigManager().save(accountConfig);
            } catch (IOException e) {
                logger.warn("[TE] An error occurred while saving the account configuration file!");
            }
        }
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
        int playerLevel = getJobLevel(jobName, player);
        int playerCurExp = getJobExp(jobName, player);
        int expToLevel = getExpToLevel(player);

        if (playerCurExp >= expToLevel) {
            playerLevel += 1;
            playerCurExp -= expToLevel;

            if (databaseActive) {
                SQLQuery.builder(sqlHandler.dataSource)
                        .update("totaleconomy.levels")
                        .set(jobName)
                        .equals(String.valueOf(playerLevel))
                        .where("uid")
                        .equals(playerUUID.toString())
                        .build();

                SQLQuery.builder(sqlHandler.dataSource)
                        .update("totaleconomy.experience")
                        .set(jobName)
                        .equals(String.valueOf(playerCurExp))
                        .where("uid")
                        .equals(playerUUID.toString())
                        .build();

                // TODO: Handle any issues that arise whilst updating
            } else {
                accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "level").setValue(playerLevel);
                accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").setValue(playerCurExp);
            }

            player.sendMessage(Text.of(TextColors.GRAY, "Congratulations, you are now a level ", TextColors.GOLD,
                    playerLevel, " ", titleize(jobName)));
        }
    }

    /**
     * Checks the jobs config for the jobName.
     *
     * @param jobName name of the job
     * @return boolean if the job exists or not
     */
    public boolean jobExists(String jobName) {
        if (jobsConfig.getNode(jobName.toLowerCase()).getValue() != null) {
            return true;
        }

        return false;
    }

    /**
     * Convert strings to titles (title -> Title)
     *
     * @param input the string to be titleized
     * @return String the titileized version of the input
     */
    public String titleize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    /**
     * Notifies a player when they are rewarded for completing a job action
     *
     * @param amount
     */
    private void notifyPlayer(Player player, BigDecimal amount) {
        Currency defaultCurrency = totalEconomy.getDefaultCurrency();

        player.sendMessage(Text.of(TextColors.GOLD, defaultCurrency.format(amount, defaultCurrency.getDefaultFractionDigits()), TextColors.GRAY, " has been added to your balance."));
    }

    /**
     * Set the users's job.
     *
     * @param user User object
     * @param jobName name of the job
     */
    public boolean setJob(User user, String jobName) {
        UUID userUUID = user.getUniqueId();

        // Just in case the job name was not passed in as lowercase, make it lowercase
        jobName = jobName.toLowerCase();

        if (databaseActive) {
            SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                    .update("totaleconomy.accounts")
                    .set("job")
                    .equals(jobName)
                    .where("uid")
                    .equals(userUUID.toString())
                    .build();

            if (sqlQuery.getRowsAffected() > 0) {
                return true;
            } else {
                logger.warn("[TE] An error occurred while changing the job of " + user.getUniqueId() + "/" + user.getName() + "!");
                return false;
            }
        } else {
            accountConfig.getNode(userUUID.toString(), "job").setValue(jobName);

            accountConfig.getNode(userUUID.toString(), "jobstats", jobName, "level").setValue(
                    accountConfig.getNode(userUUID.toString(), "jobstats", jobName, "level").getInt(1));

            accountConfig.getNode(userUUID.toString(), "jobstats", jobName, "exp").setValue(
                    accountConfig.getNode(userUUID.toString(), "jobstats", jobName, "exp").getInt(0));

            try {
                accountManager.getConfigManager().save(accountConfig);
            } catch (IOException e) {
                logger.warn("[TE] An error occurred while changing the job of " + user.getUniqueId() + "/" + user.getName() + "!");
            }

            return true;
        }

    }

    /**
     * Get a jobSet by name
     *
     * @return {@link Optional<TEJobSet>} jobSet
     */
    public Optional<TEJobSet> getJobSet(String name) {
        return Optional.ofNullable(jobSets.getOrDefault(name, null));
    }

    /**
     * Get the user's current job as a String for output
     *
     * @param user
     * @return String the job the user currently has
     */
    public String getPlayerJob(User user) {
        UUID uuid = user.getUniqueId();

        if (databaseActive) {
            SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                    .select("job")
                    .from("totaleconomy.accounts")
                    .where("uid")
                    .equals(uuid.toString())
                    .build();

            return sqlQuery.getString("unemployed").toLowerCase();
        } else {
            return accountConfig.getNode(user.getUniqueId().toString(), "job").getString("unemployed").toLowerCase();
        }
    }

    /**
     * Get a TEJob object by a job name
     *
     * @param jobName Name of the job
     * @param tryUnemployed Whether or not to try returning the unemployed job when the job wasn't found
     * @return {@link TEJob} the job; {@code null} for not found
     */
    public Optional<TEJob> getJob(String jobName, boolean tryUnemployed) {
        TEJob job = jobsMap.getOrDefault(jobName, null);

        if (job != null || !tryUnemployed)
            return Optional.ofNullable(job);

        return getJob("unemployed", false);
    }

    /**
     * Get the players level for the passed in job
     *
     * @param jobName the name of the job
     * @param user the user object
     * @return int the job level
     */
    public int getJobLevel(String jobName, User user) {
        UUID playerUUID = user.getUniqueId();

        // Just in case the job name was not passed in as lowercase, make it lowercase
        jobName = jobName.toLowerCase();

        if (!jobName.equals("unemployed")) {
            if (databaseActive) {
                SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                        .select(jobName)
                        .from("totaleconomy.levels")
                        .where("uid")
                        .equals(playerUUID.toString())
                        .build();

                return sqlQuery.getInt(1);
            } else {
                return accountConfig.getNode(user.getUniqueId().toString(), "jobstats", jobName, "level").getInt(1);
            }
        }

        return 1;
    }

    /**
     * Get the players exp for the passed in job.
     *
     * @param jobName the name of the job
     * @param user the user object
     * @return int the job exp
     */
    public int getJobExp(String jobName, User user) {
        UUID playerUUID = user.getUniqueId();

        // Just in case the job name was not passed in as lowercase, make it lowercase
        jobName = jobName.toLowerCase();

        if (!jobName.equals("unemployed")) {
            if (databaseActive) {
                SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                        .select(jobName)
                        .from("totaleconomy.experience")
                        .where("uid")
                        .equals(playerUUID.toString())
                        .build();

                return sqlQuery.getInt(0);
            } else {
                return accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").getInt(0);
            }
        }

        return 0;
    }

    /**
     * Get the exp required to level.
     *
     * @param user user object
     * @return int the amount of exp needed to level
     */
    public int getExpToLevel(User user) {
        String jobName = getPlayerJob(user);
        int playerLevel = getJobLevel(jobName, user);

        // TODO: Custom algorithm for this, set from config
        return playerLevel * 100;
    }

    /**
     * Gets a comma separated string of all of the jobs currently in the jobs config.
     *
     * @return Text Comma separated string of jobs
     */
    public Text getJobList() {
        List<Text> texts = new ArrayList<>();

        jobsMap.forEach((jobName, jobObject) -> texts.add(Text.of(
                TextActions.runCommand("/job set " + jobName),
                TextActions.showText(Text.of("Click to change job")),
                jobName))
        );

        return Text.joinWith(Text.of(", "), texts.toArray(new Text[texts.size()]));
    }

    /**
     * Getter for the jobSet configuration
     *
     * @return ConfigurationNode the jobSet configuration
     */
    public ConfigurationNode getJobSetConfig() {
        return jobSetsConfig;
    }

    /**
     * Getter for the jobs configuration
     *
     * @return ConfigurationNode the jobs configuration
     */
    public ConfigurationNode getJobsConfig() {
        return jobsConfig;
    }

    /**
     * Checks sign contents and converts it to a "Job Changing" sign if conditions are met
     *
     * @param event ChangeSignEvent
     */
    @Listener
    public void onJobSignCheck(ChangeSignEvent event) {
        SignData data = event.getText();
        Text lineOne = data.lines().get(0);
        Text lineTwo = data.lines().get(1);
        String lineOnePlain = lineOne.toPlain();
        String lineTwoPlain = lineTwo.toPlain();

        if (lineOnePlain.equals("[TEJobs]")) {
            lineOne = lineOne.toBuilder().color(TextColors.GOLD).build();

            String jobName = lineTwoPlain.toLowerCase();
            if (jobExists(lineTwoPlain)) {
                lineTwo = Text.of(jobName).toBuilder().color(TextColors.GRAY).build();
            } else {
                lineTwo = Text.of(jobName).toBuilder().color(TextColors.RED).build();
            }

            data.set(data.lines().set(0, lineOne));
            data.set(data.lines().set(1, lineTwo));
            data.set(data.lines().set(2, Text.of()));
            data.set(data.lines().set(3, Text.of()));
        }
    }

    /**
     * Called when a player clicks a sign. If the clicked sign is a "Job Changing" sign then the player's job will
     * be changed on click.
     *
     * @param event InteractBlockEvent
     */
    @Listener
    public void onSignInteract(InteractBlockEvent event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();

            if (event.getTargetBlock().getLocation().isPresent()) {
                Optional<TileEntity> tileEntityOpt = event.getTargetBlock().getLocation().get().getTileEntity();

                if (tileEntityOpt.isPresent()) {
                    TileEntity tileEntity = tileEntityOpt.get();

                    if (tileEntity instanceof Sign) {
                        Sign sign = (Sign) tileEntity;
                        Optional<SignData> data = sign.getOrCreate(SignData.class);

                        if (data.isPresent()) {
                            SignData signData = data.get();
                            Text lineOneText = signData.lines().get(0);
                            Text lineTwoText = signData.lines().get(1);
                            String lineOne = lineOneText.toPlain();
                            String lineTwo = lineTwoText.toPlain().toLowerCase();

                            if (lineOne.equals("[TEJobs]")) {
                                if (jobExists(lineTwo)) {
                                    if (setJob(player, lineTwo)) {
                                        player.sendMessage(Text.of(TextColors.GRAY, "Job changed to: ", TextColors.GOLD, lineTwo));
                                    } else {
                                        player.sendMessage(Text.of(TextColors.RED, "[TE] Failed to set job. Contact your administrator."));
                                    }
                                } else {
                                    player.sendMessage(Text.of(TextColors.RED, "[TE] Sorry, this job does not exist"));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Used for the break option in jobs. Will check if the job has the break node and if it does it will check if the
     * block that was broken is present in the config of the player's job. If it is, it will grab the job exp reward as
     * well as the pay.
     *
     * @param event ChangeBlockEvent.Break
     */
    @Listener
    public void onPlayerBlockBreak(ChangeBlockEvent.Break event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();

            String playerJob = getPlayerJob(player);
            Optional<TEJob> optPlayerJob = getJob(playerJob, true);

            BlockState state = event.getTransactions().get(0).getOriginal().getState();
            String blockName = state.getType().getName();
            Optional<UUID> blockCreator = event.getTransactions().get(0).getOriginal().getCreator();

            if (optPlayerJob.isPresent()) {
                Optional<TEActionReward> reward = Optional.empty();
                List<String> sets = optPlayerJob.get().getSets();

                for (String s : sets) {
                    Optional<TEJobSet> optSet = getJobSet(s);

                    if (!optSet.isPresent()) {
                        logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                        continue;
                    }

                    Optional<TEActionReward> currentReward = optSet.get().getRewardFor("break", blockName);

                    // Use the one giving higher exp in case of duplicates (faster comparision than BD)
                    if (reward.isPresent() && currentReward.isPresent()) {
                        if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                            reward = currentReward;
                        }
                    } else {
                        reward = currentReward;
                    }
                }

                if (reward.isPresent()) {
                    int expAmount = reward.get().getExpReward();
                    BigDecimal payAmount = reward.get().getMoneyReward();
                    Optional<String> growthTrait = reward.get().getGrowthTrait();

                    // If there is a growth trait calculate a percentage to compensate only partly grown crops
                    if (growthTrait.isPresent()) {
                        Optional<BlockTrait<?>> optTrait = state.getTrait(growthTrait.get());

                        if (!optTrait.isPresent()) {
                            logger.warn("Job " + playerJob + " break \"" + blockName + "\" has trait entry that couldn't be found on the block.");
                            return;
                        }

                        if (!Integer.class.isAssignableFrom(optTrait.get().getValueClass())) {
                            logger.warn("Job " + playerJob + " break \"" + blockName + "\" has trait entry that cannot be read as Integer.");
                            return;
                        }

                        Optional<Integer> optVal = state.getTraitValue((BlockTrait<Integer>) optTrait.get());

                        if (!optVal.isPresent()) {
                            logger.warn("Job " + playerJob + " break \"" + blockName + "\" has trait entry that couldn't be read as Integer.");
                            return;
                        }

                        // Calculate percentages
                        Integer val = optVal.get();
                        Collection<Integer> optValues = (Collection<Integer>) optTrait.get().getPossibleValues();
                        Integer max = optValues.stream().max(Comparator.comparingInt(Integer::intValue)).orElse(0);
                        Integer min = optValues.stream().min(Comparator.comparingInt(Integer::intValue)).orElse(0);
                        double perc = (double) (val - min) / (double) (max - min);
                        payAmount = payAmount.multiply(BigDecimal.valueOf(perc));
                        expAmount = (int) (expAmount * perc);
                    } else if (blockCreator.isPresent()) {
                        // A player placed the block and it doesn't indicate growth -> Do not pay to prevent exploits
                        return;
                    }

                    boolean notify = accountManager.getJobNotificationState(player);
                    TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                    if (notify) {
                        notifyPlayer(player, payAmount);
                    }

                    addExp(player, expAmount);
                    playerAccount.deposit(totalEconomy.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                    checkForLevel(player);
                }
            }
        }
    }

    /**
     * Used for the place option in jobs. Will check if the job has the place node and if it does it will check if the
     * block that was placed is present in the config of the player's job. If it is, it will grab the job exp reward as
     * well as the pay.
     *
     * @param event ChangeBlockEvent.Place
     */
    @Listener
    public void onPlayerPlaceBlock(ChangeBlockEvent.Place event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();

            String playerJob = getPlayerJob(player);
            Optional<TEJob> optPlayerJob = getJob(playerJob, true);

            String blockName = event.getTransactions().get(0).getFinal().getState().getType().getName();

            if (optPlayerJob.isPresent()) {
                Optional<TEActionReward> reward = Optional.empty();
                List<String> sets = optPlayerJob.get().getSets();

                for (String s : sets) {
                    Optional<TEJobSet> optSet = getJobSet(s);

                    if (!optSet.isPresent()) {
                        logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                        continue;
                    }

                    Optional<TEActionReward> currentReward = optSet.get().getRewardFor("place", blockName);

                    // Use the one giving higher exp in case of duplicates (faster comparision than BD)
                    if (reward.isPresent() && currentReward.isPresent()) {
                        if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                            reward = currentReward;
                        }
                    } else {
                        reward = currentReward;
                    }
                }

                if (reward.isPresent()) {
                    int expAmount = reward.get().getExpReward();
                    BigDecimal payAmount = reward.get().getMoneyReward();
                    boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                    TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                    if (notify) {
                        notifyPlayer(player, payAmount);
                    }

                    addExp(player, expAmount);
                    playerAccount.deposit(totalEconomy.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                    checkForLevel(player);
                }
            }
        }
    }

    /**
     * Used for the break option in jobs. Will check if the job has the break node and if it does it will check if the
     * block that was broken is present in the config of the player's job. If it is, it will grab the job exp reward as
     * well as the pay.
     *
     * @param event DestructEntityEvent.Death
     */
    @Listener
    public void onPlayerKillEntity(DestructEntityEvent.Death event) {
        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);

        if (optDamageSource.isPresent()) {
            EntityDamageSource damageSource = optDamageSource.get();
            Entity killer = damageSource.getSource();
            Entity victim = event.getTargetEntity();

            if (!(killer instanceof Player)) {
                // If a projectile was shot to kill an entity, this will grab the player who shot it
                Optional<UUID> damageCreator = damageSource.getSource().getCreator();

                if (damageCreator.isPresent())
                    killer = Sponge.getServer().getPlayer(damageCreator.get()).get();
            }

            if (killer instanceof Player) {
                Player player = (Player) killer;
                UUID playerUUID = player.getUniqueId();
                String victimName = victim.getType().getName();

                String playerJob = getPlayerJob(player);
                Optional<TEJob> optPlayerJob = getJob(playerJob, true);

                if (optPlayerJob.isPresent()) {
                    Optional<TEActionReward> reward = Optional.empty();
                    List<String> sets = optPlayerJob.get().getSets();

                    for (String s : sets) {
                        Optional<TEJobSet> optSet = getJobSet(s);

                        if (!optSet.isPresent()) {
                            logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                            continue;
                        }

                        Optional<TEActionReward> currentReward = optSet.get().getRewardFor("kill", victimName);

                        // Use the one giving higher exp in case of duplicates (faster comparision than BD)
                        if (reward.isPresent() && currentReward.isPresent()) {
                            if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                                reward = currentReward;
                            }
                        } else {
                            reward = currentReward;
                        }
                    }

                    if (reward.isPresent()) {
                        int expAmount = reward.get().getExpReward();
                        BigDecimal payAmount = reward.get().getMoneyReward();
                        boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                        if (notify) {
                            notifyPlayer(player, payAmount);
                        }

                        addExp(player, expAmount);
                        playerAccount.deposit(totalEconomy.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                        checkForLevel(player);
                    }
                }
            }
        }
    }

    /**
     * Used for the catch option in jobs. Will check if the job has the catch node and if it does it will check if the
     * item that was caught is present in the config of the player's job. If it is, it will grab the job exp reward as
     * well as the pay.
     *
     * @param event FishingEvent.Stop
     */
    @Listener
    public void onPlayerFish(FishingEvent.Stop event) {
        if (event.getCause().first(Player.class).isPresent()) {
            // no transaction, so execution can stop
            if (event.getItemStackTransaction().size() == 0) {
                return;
            }

            Transaction<ItemStackSnapshot> itemTransaction = event.getItemStackTransaction().get(0);
            ItemStack itemStack = itemTransaction.getFinal().createStack();
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();

            String playerJob = getPlayerJob(player);
            Optional<TEJob> optPlayerJob = getJob(playerJob, true);

            if (optPlayerJob.isPresent()) {
                if (itemStack.get(FishData.class).isPresent()) {
                    FishData fishData = itemStack.get(FishData.class).get();
                    String fishName = fishData.type().get().getName();

                    Optional<TEActionReward> reward = Optional.empty();
                    List<String> sets = optPlayerJob.get().getSets();

                    for (String s : sets) {
                        Optional<TEJobSet> optSet = getJobSet(s);

                        if (!optSet.isPresent()) {
                            logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                            continue;
                        }

                        Optional<TEActionReward> currentReward = optSet.get().getRewardFor("catch", fishName);

                        // Use the one giving higher exp in case of duplicates (faster comparision than BD)
                        if (reward.isPresent() && currentReward.isPresent()) {
                            if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                                reward = currentReward;
                            }
                        } else {
                            reward = currentReward;
                        }
                    }

                    if (reward.isPresent()) {
                        int expAmount = reward.get().getExpReward();
                        BigDecimal payAmount = reward.get().getMoneyReward();
                        boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                        if (notify) {
                            notifyPlayer(player, payAmount);
                        }

                        addExp(player, expAmount);
                        playerAccount.deposit(totalEconomy.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                        checkForLevel(player);
                    }
                }
            }
        }
    }
}
