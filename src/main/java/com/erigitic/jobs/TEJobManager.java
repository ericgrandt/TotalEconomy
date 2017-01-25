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
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.manipulator.mutable.item.FishData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
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
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TEJobManager {

    // We only need one instance of this
    public static final IDefaultJobSet[] defaultJobSets = {
            new FishermanJobSet(),
            new LumberjackJobSet(),
            new MinerJobSet(),
            new WarriorJobSet()
    };

    // We only need one instance of this
    public static final IDefaultJob[] defaultJobs = {
            new UnemployedJob(),
            new FishermanJob(),
            new LumberjackJob(),
            new MinerJob(),
            new WarriorJob()
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
    private Map<String, TEJob> jobs;

    public TEJobManager(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();
        accountConfig = accountManager.getAccountConfig();
        logger = totalEconomy.getLogger();

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
                    Optional<TEJob> optJob = getPlayerTEJob(player);
                    if (!optJob.isPresent()) {
                        // This should NOT happen unless the admin has removed the "unemployed" job... or something weird happened
                        // Either way, the admin should be notified about this
                        player.sendMessage(Text.of(TextColors.RED, "[TE] Cannot pay your salary! Contact your administrator!"));

                        return;
                    }

                    if (optJob.get().salaryEnabled()) {
                        BigDecimal salary = optJob.get().getSalary();
                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                        TransactionResult result = playerAccount.deposit(
                                        totalEconomy.getDefaultCurrency(),
                                        salary,
                                        Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer()))
                                );
                        if (result.getResult() == ResultType.SUCCESS) {
                            player.sendMessage(Text.of(TextColors.GRAY, "Your salary of ", TextColors.GOLD,
                                    totalEconomy.getCurrencySymbol(), salary, TextColors.GRAY, " has just been paid."));
                        } else {
                            player.sendMessage(Text.of(
                                    TextColors.RED, "[TE] Failed to pay your salary! You may want to contact your admin - TransactionResult:", result.getResult().toString()
                            ));
                        }
                    }
                }
        }).delay(jobsConfig.getNode("salarydelay").getInt(), TimeUnit.SECONDS).interval(jobsConfig.getNode("salarydelay")
                .getInt(), TimeUnit.SECONDS).name("Pay Day").submit(totalEconomy);
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
        jobs = new HashMap();
        reloadJobsConfig();
    }

    /**
     * Reload the jobSet config
     */
    public boolean reloadJobSetConfig() {
        try {
            jobSetsConfig = jobSetsLoader.load();

            if (!jobSetsFile.exists()) {
                ConfigurationNode jobs = jobSetsConfig.getNode("sets");

                // Install default jobs
                for (IDefaultJobSet j : defaultJobSets) {
                    j.applyOnNode(jobs);
                }

                jobSetsLoader.save(jobSetsConfig);
            }

            ConfigurationNode sets = jobSetsConfig.getNode("sets");
            Map<?, ?> setMap = sets.getChildrenMap();

            setMap.forEach((k, v) -> {
                if ((k instanceof String) && (v instanceof ConfigurationNode)) {
                    TEJobSet set = TEJobSet.of((ConfigurationNode) v);

                    if (set!=null)
                        jobSets.put((String) k, set);
                    else
                        logger.warn("Unable to load set: " + k);
                }
            });

            return true;
        } catch (IOException e) {
            logger.warn("Could not create/load jobs config file!");

            return false;
        }
    }

    /**
     * Reload the jobs config
     */
    public boolean reloadJobsConfig() {
        try {
            jobsConfig = jobsLoader.load();

            if (!jobsFile.exists()) {
                ConfigurationNode jobs = jobsConfig.getNode("jobs");

                // Install default jobs
                for (IDefaultJob j : defaultJobs) {
                    j.applyOnNode(jobs);
                }

                jobsConfig.getNode("salarydelay").setValue(300);

                jobsLoader.save(jobsConfig);
            }

            ConfigurationNode sets = jobsConfig.getNode("jobs");
            Map<?, ?> setMap = sets.getChildrenMap();

            setMap.forEach((k, v) -> {
                if ((k instanceof String) && (v instanceof ConfigurationNode)) {
                    TEJob job = TEJob.of(((ConfigurationNode) v));

                    if (job!=null)
                        jobs.put((String) k, job);
                    else
                        logger.warn("Unable to load set: " + k);
                }
            });

            return true;
        } catch (IOException e) {
            logger.warn("Could not create/load jobs config file!");

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
     * @param player player object
     * @param expAmount amount of exp to be gained
     */
    public void addExp(Player player, int expAmount) {
        String jobName = getPlayerJob(player);
        UUID playerUUID = player.getUniqueId();
        int curExp = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").getInt();

        accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").setValue(curExp + expAmount);

        try {
            accountManager.getConfigManager().save(accountConfig);
        } catch (IOException e) {
            logger.warn("Problem saving account config!");
        }

        if (accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean())
            player.sendMessage(Text.of(TextColors.GRAY, "You have gained ", TextColors.GOLD, expAmount, TextColors.GRAY,
                    " exp in the ", TextColors.GOLD, jobName, TextColors.GRAY, " job."));
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
        int playerLevel = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "level").getInt();
        int playerCurExp = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").getInt();
        int expToLevel = getExpToLevel(player);

        if (playerCurExp >= expToLevel) {
            accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "level").setValue(playerLevel + 1);
            accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").setValue(playerCurExp - expToLevel);
            player.sendMessage(Text.of(TextColors.GRAY, "Congratulations, you are now a level ", TextColors.GOLD,
                    playerLevel + 1, " ", titleize(jobName)));
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
     * Set the player's job.
     *
     * @param player Player object
     * @param jobName name of the job
     */
    public void setJob(Player player, String jobName) {
        UUID playerUUID = player.getUniqueId();

        accountConfig.getNode(playerUUID.toString(), "job").setValue(jobName);

        // Set level if not of type int or null
        accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "level").setValue(
                accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "level").getInt(1)
        );

        // See above
        accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").setValue(
                accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "exp").getInt(0)
        );

        player.sendMessage(Text.of(TextColors.GRAY, "Your job has been changed to ", TextColors.GOLD, jobName));

        try {
            accountManager.getConfigManager().save(accountConfig);
        } catch (IOException e) {
            logger.warn("Could not save account config while setting job!");
            player.sendMessage(Text.of(TextColors.RED, "[TE] Job saving for you failed! You might loose the change upon restart/reload."));
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
     * Get the player's current job
     *
     * @param player
     * @return String the job the player currently has
     */
    public String getPlayerJob(Player player) {
        // For convenience always cast job name to lowercase
        return accountConfig.getNode(player.getUniqueId().toString(), "job").getString("unemployed").toLowerCase();
    }

    /**
     * Get the player's current TEJob instance
     *
     * @param player
     * @return {@link TEJob} the job the player currently has
     */
    public Optional<TEJob> getPlayerTEJob(Player player) {
        String jobName = getPlayerJob(player);

        return getJob(jobName, true);
    }

    /**
     * Get a job by its name
     *
     * @param jobName name of the job
     * @param tryUnemployed whether or not to try returning the unemployed job when the job wasn't found
     * @return {@link TEJob} the job; {@code null} for not found
     */
    public Optional<TEJob> getJob(String jobName, boolean tryUnemployed) {
        TEJob job = jobs.getOrDefault(jobName, null);

        if (job != null || !tryUnemployed)
            return Optional.ofNullable(job);

        return getJob("unemployed", false);
    }

    /**
     * Get the players exp for the passed in job.
     *
     * @param jobName the name of the job
     * @param player the player object
     * @return int the job exp
     */
    public int getJobExp(String jobName, Player player) {
        return accountConfig.getNode(player.getUniqueId().toString(), "jobstats", jobName, "exp").getInt(0);
    }

    /**
     * Get the players level for the passed in job
     *
     * @param jobName the name of the job
     * @param player the player object
     * @return int the job level
     */
    public int getJobLevel(String jobName, Player player) {
        return accountConfig.getNode(player.getUniqueId().toString(), "jobstats", jobName, "level").getInt(1);
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
        int playerLevel = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName, "level").getInt(1);

        return playerLevel * 100;
    }

    /**
     * Gets a list of all of the jobs currently in the jobs config.
     *
     * @return String list of jobs
     */
    public String getJobList() {
        StringBuilder b = new StringBuilder();

        jobs.forEach((j, o) -> b.append(j).append(", "));

        String s = b.toString();

        //Remove the last ", "
        return s.substring(0, s.length() - 2);
    }

    /**
     * Getter for the jobSet configuration
     *
     * @return {@link ConfigurationNode} the jobSet configuration
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
                                    setJob(player, lineTwo);
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
            Optional<TEJob> optPlayerJob = getPlayerTEJob(player);
            String blockName = event.getTransactions().get(0).getOriginal().getState().getType().getName();
            Optional<UUID> blockCreator = event.getTransactions().get(0).getOriginal().getCreator();

            if (optPlayerJob.isPresent()) {
                // Prevent blocks placed by other players from counting towards a job
                if (!blockCreator.isPresent()) {
                    Optional<TEActionReward> reward = Optional.empty();
                    List<String> sets = optPlayerJob.get().getSets();

                    for (String s : sets) {
                        Optional<TEJobSet> optSet = getJobSet(s);
                        if (!optSet.isPresent()) {
                            logger.warn("Job " + getPlayerJob(player) + " has the nonexistent set \"" + s + "\"");

                            continue;
                        }

                        reward = optSet.get().getRewardFor("break", blockName);

                        // TODO: Priorities?
                        if (reward.isPresent()) break;
                    }

                    if (reward.isPresent()) {
                        int expAmount = reward.get().getExpReward();
                        BigDecimal payAmount = reward.get().getMoneyReward();
                        boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                        if (notify) {
                            player.sendMessage(Text.of(TextColors.GOLD, accountManager.getDefaultCurrency().getSymbol(), payAmount, TextColors.GRAY, " has been added to your balance."));
                        }

                        addExp(player, expAmount);
                        playerAccount.deposit(accountManager.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                        checkForLevel(player);
                    }
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
            Optional<TEJob> optPlayerJob = getPlayerTEJob(player);
            String blockName = event.getTransactions().get(0).getFinal().getState().getType().getName();

            if (optPlayerJob.isPresent()) {
                Optional<TEActionReward> reward = Optional.empty();
                List<String> sets = optPlayerJob.get().getSets();

                for (String s : sets) {
                    Optional<TEJobSet> optSet = getJobSet(s);

                    if (!optSet.isPresent()) {
                        logger.warn("Job " + getPlayerJob(player) + " has nonexistent set \"" + s + "\"");

                        continue;
                    }

                    reward = optSet.get().getRewardFor("place", blockName);

                    //TODO: Priorities?
                    if (reward.isPresent()) break;
                }
                if (reward.isPresent()) {
                    int expAmount = reward.get().getExpReward();
                    BigDecimal payAmount = reward.get().getMoneyReward();
                    boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                    TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                    if (notify) {
                        player.sendMessage(Text.of(TextColors.GOLD, accountManager.getDefaultCurrency().getSymbol(), payAmount, TextColors.GRAY, " has been added to your balance."));
                    }

                    addExp(player, expAmount);
                    playerAccount.deposit(accountManager.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
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
                Optional<TEJob> optPlayerJob = getPlayerTEJob(player);

                if (optPlayerJob.isPresent()) {
                    Optional<TEActionReward> reward = Optional.empty();
                    List<String> sets = optPlayerJob.get().getSets();

                    for (String s : sets) {
                        Optional<TEJobSet> optSet = getJobSet(s);

                        if (!optSet.isPresent()) {
                            logger.warn("Job " + getPlayerJob(player) + " has nonexistent set \"" + s + "\"");

                            continue;
                        }

                        reward = optSet.get().getRewardFor("kill", victimName);

                        //TODO: Priorities?
                        if (reward.isPresent()) break;
                    }
                    if (reward.isPresent()) {
                        int expAmount = reward.get().getExpReward();
                        BigDecimal payAmount = reward.get().getMoneyReward();
                        boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                        if (notify) {
                            player.sendMessage(Text.of(TextColors.GOLD, accountManager.getDefaultCurrency().getSymbol(), payAmount, TextColors.GRAY, " has been added to your balance."));
                        }

                        addExp(player, expAmount);
                        playerAccount.deposit(accountManager.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
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
            Transaction<ItemStackSnapshot> itemTransaction = event.getItemStackTransaction().get(0);
            ItemStack itemStack = itemTransaction.getFinal().createStack();
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();
            Optional<TEJob> optPlayerJob = getPlayerTEJob(player);

            if (optPlayerJob.isPresent()) {
                if (itemStack.get(FishData.class).isPresent()) {
                    FishData fishData = itemStack.get(FishData.class).get();
                    String fishName = fishData.type().get().getName();

                    Optional<TEActionReward> reward = Optional.empty();
                    List<String> sets = optPlayerJob.get().getSets();

                    for (String s : sets) {
                        Optional<TEJobSet> optSet = getJobSet(s);

                        if (!optSet.isPresent()) {
                            logger.warn("Job " + getPlayerJob(player) + " has nonexistent set \"" + s + "\"");

                            continue;
                        }

                        reward = optSet.get().getRewardFor("catch", fishName);

                        //TODO: Priorities?
                        if (reward.isPresent()) break;
                    }
                    if (reward.isPresent()) {
                        int expAmount = reward.get().getExpReward();
                        BigDecimal payAmount = reward.get().getMoneyReward();
                        boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                        if (notify) {
                            player.sendMessage(Text.of(TextColors.GOLD, accountManager.getDefaultCurrency().getSymbol(), payAmount, TextColors.GRAY, " has been added to your balance."));
                        }

                        addExp(player, expAmount);
                        playerAccount.deposit(accountManager.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                        checkForLevel(player);
                    }
                }
            }
        }
    }
}