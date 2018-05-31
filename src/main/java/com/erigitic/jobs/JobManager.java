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
import com.erigitic.config.account.TEAccountBase;
import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.SqlManager;
import com.erigitic.sql.SqlQuery;
import com.erigitic.util.MessageManager;
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
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class JobManager {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private MessageManager messageManager;
    private Logger logger;

    private File jobSetsFile;
    private ConfigurationLoader<CommentedConfigurationNode> jobSetsLoader;
    private ConfigurationNode jobSetsConfig;
    private Map<String, TEJobSet> jobSets;

    private File jobsFile;
    private ConfigurationLoader<CommentedConfigurationNode> jobsLoader;
    private ConfigurationNode jobsConfig;
    private Map<UUID, TEJob> jobsMap;

    private boolean databaseEnabled;
    private SqlManager sqlManager;

    public JobManager(TotalEconomy totalEconomy, AccountManager accountManager, MessageManager messageManager, Logger logger) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.messageManager = messageManager;
        this.logger = logger;

        databaseEnabled = totalEconomy.isDatabaseEnabled();
        if (databaseEnabled) {
            sqlManager = totalEconomy.getSqlManager();
        }

        setupConfig();

        if (totalEconomy.isJobSalaryEnabled()) {
            startSalaryTask();
        }
    }

    /**
     * Start the timer that pays out the salary to each player after a specified time in seconds
     */
    private void startSalaryTask() {
        Scheduler scheduler = totalEconomy.getGame().getScheduler();
        Task.Builder payTask = scheduler.createTaskBuilder();

        payTask.execute(() -> {
            for (Player player : totalEconomy.getServer().getOnlinePlayers()) {
                Optional<TEJob> optJob = getTEJobOfPlayer(player,true);

                if (!optJob.isPresent()) {
                    player.sendMessage(Text.of(TextColors.RED, "[TE] Cannot pay your salary! Contact your administrator!"));
                    return;
                }

                if (optJob.get().salaryEnabled()) {
                    BigDecimal salary = optJob.get().getSalary();
                    TEAccountBase playerAccount = (TEAccountBase) accountManager.getOrCreateAccount(player.getUniqueId()).get();

                    TransactionResult result = playerAccount.deposit(totalEconomy.getDefaultCurrency(), salary, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

                    if (result.getResult() == ResultType.SUCCESS) {
                        Map<String, String> messageValues = new HashMap<>();
                        messageValues.put("amount", totalEconomy.getDefaultCurrency().format(salary).toPlain());

                        player.sendMessage(messageManager.getMessage("jobs.salary", messageValues));
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
        jobSetsFile = new File(totalEconomy.getConfigDir(), "jobsets.conf");
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
            if (!jobSetsFile.exists()) {
                totalEconomy.getPluginContainer().getAsset("jobsets.conf").get().copyToFile(jobSetsFile.toPath());
            }

            jobSetsConfig = jobSetsLoader.load();
            ConfigurationNode sets = jobSetsConfig.getNode("sets");

            sets.getChildrenMap().forEach((setName, setNode) -> {
                if (setNode != null) {
                    TEJobSet jobSet = new TEJobSet(setNode);

                    jobSets.put((String) setName, jobSet);
                }
            });

            return true;
        } catch (IOException e) {
            logger.warn("An error occurred while creating/loading the jobSets configuration file!");

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
            if (!jobsFile.exists()) {
                totalEconomy.getPluginContainer().getAsset("jobs.conf").get().copyToFile(jobsFile.toPath());
            }
            
            jobsConfig = jobsLoader.load();
            ConfigurationNode jobsNode = jobsConfig.getNode("jobs");
            final Pattern UUID_PATTERN = Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89aAbB][a-fA-F0-9]{3}-[a-fA-F0-9]{12})");
            Map<String, UUID> renamedJobs = new HashMap<>();

            // Loop through each job node in the configuration file, create a TEJob object from it, and store in a HashMap
            jobsNode.getChildrenMap().forEach((k, jobNode) -> {
                if (!(k instanceof String)) {
                    return;
                }
                ConfigurationNode actualNode;

                if (jobNode != null) {
                    // Automatic assignment of UUIDs for job identification
                    UUID uuid;
                    if (!UUID_PATTERN.matcher(((String) k)).matches()) {
                        jobNode.getNode("displayname").setValue(titleize(((String) k)));
                        uuid = UUID.randomUUID();
                        actualNode = jobsNode.getNode(uuid.toString());
                        actualNode.mergeValuesFrom(jobNode);
                        // Yes - this is necessary as a bug will otherwise delete both nodes from the config.
                        actualNode.getNode("converted").setValue(true);
                        jobsNode.removeChild(k);
                        renamedJobs.put(((String) k), uuid);
                    } else {
                        uuid = UUID.fromString(((String) k));
                        actualNode = jobNode;
                    }
                    TEJob job = new TEJob(uuid, actualNode);

                    if (job.isValid()) {
                        jobsMap.put(uuid, job);
                    }
                }
            });

            if (!renamedJobs.isEmpty()) {
                // Convert the saved job stats
                // (Migration to DB is automatically included in the migrators)
                if (!databaseEnabled) {
                    accountManager.getAccountConfig().getChildrenList()
                                  .forEach(account -> {
                                      ConfigurationNode jobStats = account.getNode("jobstats");
                                      renamedJobs.entrySet()
                                                 .stream()
                                                 .filter(entry -> !jobStats.getNode(entry.getKey()).isVirtual())
                                                 .forEach(entry -> {
                                                     jobStats.getNode(entry.getValue().toString()).mergeValuesFrom(jobStats.getNode(entry.getKey()));
                                                     jobStats.removeChild(entry.getKey());
                                                 });
                                  });
                }
                jobsLoader.save(jobsConfig);
            }

            return true;
        } catch (IOException e) {
            logger.warn("An error occurred while creating/loading the jobs configuration file!");

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
        UUID playerUUID = player.getUniqueId();
        Optional<TEJob> playerJob = getTEJobOfPlayer(player, false);
        boolean jobNotifications = accountManager.getJobNotificationState(player);

        if (!playerJob.isPresent()) {
            return;
        }

        Map<String, String> messageValues = new HashMap<>();
        messageValues.put("job", titleize(playerJob.get().getName()));
        messageValues.put("exp", String.valueOf(expAmount));

        if (databaseEnabled) {
            Integer newExp = getJobExp(playerJob.get().getUniqueId(), player) + expAmount;
            String queryString = "INSERT INTO `jobs_progress` (`uid`, `experience`, `job`) VALUES (:account_uid, :exp, :job) ON DUPLICATE KEY UPDATE `experience` = :exp";

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("exp", newExp);
                query.setParameter("account_uid", player.getUniqueId().toString());
                query.setParameter("job", playerJob.get().getUniqueId());
                Integer updateCount = query.getStatement().executeUpdate();

                if ( updateCount != 1 && updateCount != 2) {
                    throw new SQLException("Unexpected update count!");
                }
            } catch (SQLException e) {
                player.sendMessage(Text.of(TextColors.RED, "[TE] Error adding experience! Consult an administrator!"));
                throw new RuntimeException("Failed to add exp to progress of " + player.getUniqueId().toString() + "/" + player.getName(), e);
            }
        } else {
            ConfigurationNode expNode = accountManager.getAccountConfig().getNode(playerUUID.toString(), "jobstats", playerJob.get().getUniqueId().toString(), "exp");
            int curExp = expNode.getInt();
            expNode.setValue(curExp + expAmount);
            totalEconomy.requestAccountConfigurationSave();
        }

        if (jobNotifications) {
            player.sendMessage(messageManager.getMessage("jobs.addexp", messageValues));
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
        UUID playerJob = getPlayerJob(player).orElse(null);

        if (playerJob == null) {
            return;
        }

        Integer playerLevel = getJobLevel(playerJob, player);
        int playerCurExp = getJobExp(playerJob, player);
        int expToLevel = getExpToLevel(player);

        if (expToLevel > -1 && playerCurExp >= expToLevel) {
            playerLevel += 1;

            Map<String, String> messageValues = new HashMap<>();
            messageValues.put("job", titleize(getJobs().get(playerJob).getName()));
            messageValues.put("level", String.valueOf(playerLevel));

            if (databaseEnabled) {
                String queryString = "INSERT INTO jobs_progress (`uid`, `job`, `level`) VALUES (:uid, :job, :level) ON DUPLICATE KEY UPDATE `level` = VALUES(`level`)";

                try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                    query.setParameter("uid", playerUUID.toString());
                    query.setParameter("job" , playerJob);
                    query.setParameter("level", playerLevel.toString());
                    Integer updateCount = query.getStatement().executeUpdate();

                    if (updateCount != 1 && updateCount != 2) {
                        throw new SQLException("Unexpected update count");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to set level for player " + playerUUID.toString(), e);
                }
            } else {
                ConfigurationNode accountConfig = accountManager.getAccountConfig();
                accountConfig.getNode(playerUUID.toString(), "jobstats", playerJob, "level").setValue(playerLevel);
                totalEconomy.requestAccountConfigurationSave();
            }

            player.sendMessage(messageManager.getMessage("jobs.levelup", messageValues));
        }
    }


    public Optional<UUID> getJobUUIDByName(String name) {
        final String search = name.trim();
        return jobsMap.values().stream()
                      .filter(j -> j.getName().equalsIgnoreCase(search))
                      .findFirst()
                      .map(TEJob::getUniqueId);
    }

    /**
     * Checks the jobs config for the jobName.
     *
     * @param uuid the uuid of the job
     * @return boolean if the job exists or not
     */
    public boolean jobExists(UUID uuid) {
        return !jobsConfig.getNode("jobs", uuid).isVirtual();
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
    private void notifyPlayer(Player player, BigDecimal amount, Currency currency) {
        Text amountText = currency.format(amount, currency.getDefaultFractionDigits());

        Map<String, String> messageValues = new HashMap<>();
        messageValues.put("amount", amountText.toPlain());

        player.sendMessage(messageManager.getMessage("jobs.notify", messageValues));
    }

    /**
     * Set the users's job.
     *
     * @param user User object
     * @param jobUUID uuid of the job
     */
    public boolean setJob(User user, UUID jobUUID) {
        UUID userUUID = user.getUniqueId();

        if (databaseEnabled) {
            String queryString = "UPDATE `accounts` SET `job` = :job WHERE `uid` = :account_uid";

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("job", jobUUID);
                query.setParameter("account_uid", user.getUniqueId().toString());

                if (query.getStatement().executeUpdate() != 1) {
                    throw new SQLException("Unexpected update count");
                }
                return true;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to set job of " + user.getUniqueId() + "/" + user.getName() + " to " + jobUUID, e);
            }
        } else {
            ConfigurationNode accountConfig = accountManager.getAccountConfig();
            accountConfig.getNode(userUUID.toString(), "job").setValue(jobUUID.toString());

            accountConfig.getNode(userUUID.toString(), "jobstats", jobUUID.toString(), "level").setValue(
                    accountConfig.getNode(userUUID.toString(), "jobstats", jobUUID.toString(), "level").getInt(1));

            accountConfig.getNode(userUUID.toString(), "jobstats", jobUUID.toString(), "exp").setValue(
                    accountConfig.getNode(userUUID.toString(), "jobstats", jobUUID.toString(), "exp").getInt(0));

            totalEconomy.requestAccountConfigurationSave();
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
     * Get the user's current TEJob representation
     *
     * @param user the user
     * @param tryUnemployed Whether or not "no job" should be "unemployed"
     */
    public Optional<TEJob> getTEJobOfPlayer(User user, boolean tryUnemployed) {
        Optional<UUID> playerJob = getPlayerJob(user);
        if (playerJob.isPresent() || tryUnemployed) {
            return getJob(playerJob.orElse(null), tryUnemployed);
        }
        return Optional.empty();
    }

    public Optional<TEJob> getTEJobWithName(String name) {
        Optional<UUID> jobUUID = getJobUUIDByName(name);
        if (jobUUID.isPresent()) {
            return getJob(jobUUID.get(), false);
        }
        return Optional.empty();
    }

    /**
     * Get the user's current job as a UUID
     *
     * @param user
     * @return String the job the user currently has
     */
    public Optional<UUID> getPlayerJob(User user) {

        if (databaseEnabled) {
            String queryString = "SELECT `job` FROM `accounts` WHERE `uid` = :account_uid";
            String resultString;

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("account_uid", user.getUniqueId().toString());
                PreparedStatement statement = query.getStatement();
                statement.executeQuery();
                ResultSet result = statement.getResultSet();

                if (!result.next()) {
                    throw new SQLException("No result");
                }
                resultString = result.getString("job");

                if (result.next()) {
                    throw new SQLException("Too many results");
                }

                if (resultString == null) {
                    return Optional.empty();
                }
                return Optional.of(UUID.fromString(resultString));

            } catch (SQLException e) {
                throw new RuntimeException("Failed to retrieve job for player: " + user.getUniqueId().toString(), e);
            }
        } else {
            ConfigurationNode jobNode = accountManager.getAccountConfig().getNode(user.getUniqueId().toString(), "job");
            if (jobNode.isVirtual()) {
                return getJobUUIDByName("unemployed");
            }
            return Optional.of(UUID.fromString(jobNode.getString()));
        }
    }

    /**
     * Get a TEJob object by a job name
     *
     * @param uuid Job UUID
     * @param tryUnemployed Whether or not to try returning the unemployed job when the job wasn't found
     * @return {@link TEJob} the job; {@code null} for not found
     */
    public Optional<TEJob> getJob(UUID uuid, boolean tryUnemployed) {
        TEJob job = null;
        if (uuid != null) {
            job = jobsMap.getOrDefault(uuid, null);
        }

        if (job != null || !tryUnemployed) {
            return Optional.ofNullable(job);
        }

        Optional<UUID> uuidOpt = getJobUUIDByName("unemployed");
        if (uuidOpt.isPresent()) {
            return getJob(uuidOpt.get(), false);
        }
        return Optional.empty();
    }

    /**
     * Get the players level for the passed in job
     *
     * @param uuid the uuid of the job
     * @param user the user object
     * @return int the job level
     */
    public int getJobLevel(UUID uuid, User user) {
        if (!uuid.equals(getJobUUIDByName("unemployed").orElse(null))) {
            if (databaseEnabled) {
                String queryString = "SELECT `level` FROM `jobs_progress` WHERE `uid` = :account_uid AND `job` = :job";
                Integer resultInt = 0;

                try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                    query.setParameter("account_uid", user.getUniqueId().toString());
                    query.setParameter("job", uuid.toString());
                    PreparedStatement statement = query.getStatement();
                    statement.executeQuery();
                    ResultSet result = statement.getResultSet();

                    if (result.next()) {
                        resultInt = result.getInt("level");
                    }
                    if (result.next()) {
                        throw new SQLException("Too many results");
                    }
                    return resultInt;

                } catch (SQLException e) {
                    throw new RuntimeException("Failed to retrieve job experience for user, job: " + user.getUniqueId().toString() + "," + uuid, e);
                }
            } else {
                ConfigurationNode accountConfig = accountManager.getAccountConfig();

                return accountConfig.getNode(user.getUniqueId().toString(), "jobstats", uuid.toString(), "level").getInt(1);
            }
        }

        return 1;
    }

    /**
     * Get the players exp for the passed in job.
     *
     * @param jobUUID the uuid of the job
     * @param user the user object
     * @return int the job exp
     * @throws RuntimeException Upon SQLException | Job not found | More than one result
     */
    public int getJobExp(UUID jobUUID, User user) {
        UUID playerUUID = user.getUniqueId();

        if (!jobUUID.equals(getJobUUIDByName("unemployed").orElse(null))) {
            if (databaseEnabled) {
                String queryString = "SELECT `experience` FROM `jobs_progress` WHERE `uid` = :account_uid AND job = :job";
                Integer resultInt = 0;

                try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                    query.setParameter("account_uid", user.getUniqueId().toString());
                    query.setParameter("job", jobUUID.toString());
                    PreparedStatement statement = query.getStatement();
                    statement.executeQuery();

                    ResultSet result = statement.getResultSet();

                    if (result.next()) {
                        resultInt = result.getInt("experience");
                    }
                    if (result.next()) {
                        throw new SQLException("Too many results");
                    }
                    return resultInt;

                } catch (SQLException e) {
                    throw new RuntimeException("Failed to retrieve job experience for user, job: " + user.getUniqueId().toString() + "," + jobUUID, e);
                }
            } else {
                ConfigurationNode accountConfig = accountManager.getAccountConfig();

                return accountConfig.getNode(playerUUID.toString(), "jobstats", jobUUID.toString(), "exp").getInt(0);
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
        UUID jobUUID = getPlayerJob(user).orElse(null);
        if (jobUUID == null) {
            return -1;
        }
        int playerLevel = getJobLevel(jobUUID, user);

        int nextLevel = playerLevel + 1;
        int expToLevel = (int) ((Math.pow(nextLevel, 2) + nextLevel) / 2) * 100 - (nextLevel * 100);

        // TODO: Custom algorithm for this, set from config
        return expToLevel;
    }

    /**
     * Gets a comma separated string of all of the jobs currently in the jobs config.
     *
     * @return Text Comma separated string of jobs
     */
    public Text getJobList() {
        List<Text> texts = new ArrayList<>();

        jobsMap.forEach((jobUUID, jobObject) -> texts.add(Text.of(
                TextActions.runCommand("/job set " + jobUUID),
                TextActions.showText(Text.of("Click to change job")),
                jobObject.getName()))
        );

        return Text.joinWith(Text.of(", "), texts.toArray(new Text[texts.size()]));
    }

    /**
     * @return The jobs
     */
    public Map<UUID, TEJob> getJobs() {
        return jobsMap;
    }

    /**
     * @return The job sets
     */
    public Map<String, TEJobSet> getJobSets() {
        return jobSets;
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
            lineOne = lineOne.toBuilder().style(TextStyles.BOLD).color(TextColors.DARK_BLUE).build();

            String jobName = titleize(lineTwoPlain);
            if (getJobUUIDByName(lineTwoPlain).isPresent()) {
                lineTwo = Text.of(jobName).toBuilder().color(TextColors.BLACK).build();
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
    public void onSignInteract(InteractBlockEvent.Secondary event) {
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
                            String jobName = lineTwoText.toPlain().toLowerCase();

                            if (lineOne.equals("[TEJobs]")) {
                                Optional<UUID> jobUUIDopt = getJobUUIDByName(jobName);
                                if (jobUUIDopt.isPresent()) {
                                    if (setJob(player, jobUUIDopt.get())) {
                                        Map<String, String> messageValues = new HashMap<>();
                                        messageValues.put("job", titleize(jobName));

                                        player.sendMessage(messageManager.getMessage("jobs.sign", messageValues));
                                    } else {
                                        player.sendMessage(Text.of(TextColors.RED, "Failed to set job. Contact your administrator."));
                                    }
                                } else {
                                    player.sendMessage(Text.of(TextColors.RED, "Sorry, this job does not exist"));
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

            UUID playerJob = getPlayerJob(player).orElse(null);
            Optional<TEJob> optPlayerJob = getJob(playerJob, true);

            BlockState state = event.getTransactions().get(0).getOriginal().getState();
            String blockName = state.getType().getName();
            Optional<UUID> blockCreator = event.getTransactions().get(0).getOriginal().getCreator();

            // Enable admins to determine block information by displaying it to them - WHEN they have the flag enabled
            if (accountManager.getUserOption("totaleconomy:block-break-info", player).orElse("0").equals("1")) {
                List<BlockTrait<?>> traits = new ArrayList<>(state.getTraits());
                int count = traits.size();
                List<Text> traitTexts = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    Object traitValue = state.getTraitValue(traits.get(i)).orElse(null);
                    traitTexts.add(i, Text.of(traits.get(i).getName(), '=', traitValue != null ? traitValue.toString() : "null"));
                }

                Text t = Text.of(TextColors.GRAY, "TRAITS:\n    ", Text.joinWith(Text.of(",\n    "), traitTexts.toArray(new Text[traits.size()])));
                player.sendMessage(Text.of("Block-Name: ", blockName));
                player.sendMessage(t);
            }

            if (optPlayerJob.isPresent()) {
                Optional<TEActionReward> reward = Optional.empty();
                List<String> sets = optPlayerJob.get().getSets();

                for (String s : sets) {
                    Optional<TEJobSet> optSet = getJobSet(s);
                    if (!optSet.isPresent()) {
                        logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                        continue;
                    }

                    Optional<TEAction> action = optSet.get().getActionFor("break", blockName);
                    if (!action.isPresent()) {
                        continue;
                    }

                    Optional<TEActionReward> currentReward = action.get().evaluateBreak(logger, state, blockCreator.orElse(null));
                    if (!reward.isPresent()) {
                        reward = currentReward;
                        continue;
                    }

                    if (!currentReward.isPresent()) {
                        continue;
                    }

                    // Use the one giving higher exp in case of duplicates
                    if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                        reward = currentReward;
                    }
                }

                if (reward.isPresent()) {
                    TEAccountBase playerAccount = (TEAccountBase) accountManager.getOrCreateAccount(player.getUniqueId()).get();
                    boolean notify = accountManager.getJobNotificationState(player);
                    int expAmount = reward.get().getExpReward();
                    BigDecimal payAmount = new BigDecimal(reward.get().getMoneyReward());
                    Currency currency = totalEconomy.getDefaultCurrency();

                    if (reward.get().getCurrencyId() != null) {
                        Optional<Currency> currencyOpt = totalEconomy.getTECurrencyRegistryModule().getById("totaleconomy:" + reward.get().getCurrencyId());
                        if (currencyOpt.isPresent()) {
                            currency = currencyOpt.get();
                        }
                    }

                    if (notify) {
                        notifyPlayer(player, payAmount, currency);
                    }

                    addExp(player, expAmount);
                    playerAccount.deposit(currency, payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
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

            UUID playerJob = getPlayerJob(player).orElse(null);
            Optional<TEJob> optPlayerJob = getJob(playerJob, true);

            BlockState state = event.getTransactions().get(0).getFinal().getState();
            String blockName = state.getType().getName();

            // Enable admins to determine block information by displaying it to them - WHEN they have the flag enabled
            if (accountManager.getUserOption("totaleconomy:block-place-info", player).orElse("0").equals("1")) {
                List<BlockTrait<?>> traits = new ArrayList<>(state.getTraits());
                int count = traits.size();
                List<Text> traitTexts = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    Object traitValue = state.getTraitValue(traits.get(i)).orElse(null);
                    traitTexts.add(i, Text.of(traits.get(i).getName(), '=', traitValue != null ? traitValue.toString() : "null"));
                }

                Text t = Text.of(TextColors.GRAY, "TRAITS:\n    ", Text.joinWith(Text.of(",\n    "), traitTexts.toArray(new Text[traits.size()])));
                player.sendMessage(Text.of("Block-Name: ", blockName));
                player.sendMessage(t);
            }

            if (optPlayerJob.isPresent()) {
                Optional<TEActionReward> reward = Optional.empty();
                List<String> sets = optPlayerJob.get().getSets();

                for (String s : sets) {
                    Optional<TEJobSet> optSet = getJobSet(s);
                    if (!optSet.isPresent()) {
                        logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                        continue;
                    }

                    Optional<TEAction> action = optSet.get().getActionFor("place", blockName);
                    if (!action.isPresent()) {
                        continue;
                    }

                    Optional<TEActionReward> currentReward = action.get().evaluatePlace(logger, state);
                    if (!reward.isPresent()) {
                        reward = currentReward;
                        continue;
                    }

                    if (!currentReward.isPresent()) {
                        continue;
                    }

                    // Use the one giving higher exp in case of duplicates
                    if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                        reward = currentReward;
                    }
                }

                reward.ifPresent(teActionReward -> rewardPlayer(player, teActionReward));
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

                if (damageCreator.isPresent()) {
                    killer = Sponge.getServer().getPlayer(damageCreator.get()).get();
                }
            }

            if (killer instanceof Player) {
                Player player = (Player) killer;
                UUID playerUUID = player.getUniqueId();
                String victimName = victim.getType().getName();

                UUID playerJob = getPlayerJob(player).orElse(null);
                Optional<TEJob> optPlayerJob = getJob(playerJob, true);

                // Enable admins to determine victim information by displaying it to them - WHEN they have the flag enabled
                if (accountManager.getUserOption("totaleconomy:entity-kill-info", player).orElse("0").equals("1")) {
                    player.sendMessage(Text.of("Victim-Name: ", victimName));
                }

                if (optPlayerJob.isPresent()) {
                    Optional<TEActionReward> reward = Optional.empty();
                    List<String> sets = optPlayerJob.get().getSets();

                    for (String s : sets) {
                        Optional<TEJobSet> optSet = getJobSet(s);
                        if (!optSet.isPresent()) {
                            logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                            continue;
                        }

                        Optional<TEAction> action = optSet.get().getActionFor("kill", victimName);
                        if (!action.isPresent()) {
                            continue;
                        }

                        Optional<TEActionReward> currentReward = action.get().getReward();
                        if (!reward.isPresent()) {
                            reward = currentReward;
                            continue;
                        }

                        if (!currentReward.isPresent()) {
                            continue;
                        }

                        // Use the one giving higher exp in case of duplicates
                        if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                            reward = currentReward;
                        }
                    }

                    reward.ifPresent(teActionReward -> rewardPlayer(player, teActionReward));
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

            UUID playerJob = getPlayerJob(player).orElse(null);
            Optional<TEJob> optPlayerJob = getJob(playerJob, true);

            if (optPlayerJob.isPresent()) {
                if (itemStack.get(FishData.class).isPresent()) {
                    FishData fishData = itemStack.get(FishData.class).get();
                    String fishName = fishData.type().get().getName();

                    // Enable admins to determine fish information by displaying it to them - WHEN they have the flag enabled
                    if (accountManager.getUserOption("totaleconomy:entity-fish-info", player).orElse("0").equals("1")) {
                        player.sendMessage(Text.of("Fish-Name: ", fishName));
                    }

                    Optional<TEActionReward> reward = Optional.empty();
                    List<String> sets = optPlayerJob.get().getSets();

                    for (String s : sets) {
                        Optional<TEJobSet> optSet = getJobSet(s);

                        if (!optSet.isPresent()) {
                            logger.warn("Job " + playerJob + " has the nonexistent set \"" + s + "\"");
                            continue;
                        }

                        Optional<TEAction> action = optSet.get().getActionFor("catch", fishName);
                        if (!action.isPresent()) {
                            continue;
                        }

                        Optional<TEActionReward> currentReward = action.get().getReward();
                        if (!reward.isPresent()) {
                            reward = currentReward;
                            continue;
                        }

                        if (!currentReward.isPresent()) {
                            continue;
                        }

                        // Use the one giving higher exp in case of duplicates
                        if (currentReward.get().getExpReward() > reward.get().getExpReward()) {
                            reward = currentReward;
                        }
                    }

                    reward.ifPresent(teActionReward -> rewardPlayer(player, teActionReward));
                }
            }
        }
    }

    private void rewardPlayer(Player player, TEActionReward reward) {
        int expAmount = reward.getExpReward();
        BigDecimal payAmount = BigDecimal.valueOf(reward.getMoneyReward());
        boolean notify = accountManager.getJobNotificationState(player);
        TEAccountBase playerAccount = (TEAccountBase) accountManager.getOrCreateAccount(player.getUniqueId()).get();

        if (notify) {
            notifyPlayer(player, payAmount, totalEconomy.getDefaultCurrency());
        }

        addExp(player, expAmount);
        playerAccount.deposit(totalEconomy.getDefaultCurrency(), payAmount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
        checkForLevel(player);
    }
}
