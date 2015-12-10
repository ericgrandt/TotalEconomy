package com.erigitic.jobs;

import com.erigitic.config.AccountManager;
import com.erigitic.jobs.jobs.FishermanJob;
import com.erigitic.jobs.jobs.LumberjackJob;
import com.erigitic.jobs.jobs.MinerJob;
import com.erigitic.jobs.jobs.WarriorJob;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockTypes;
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
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Erigitic on 5/5/2015.
 */
public class TEJobs {
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private ConfigurationNode accountConfig;
    private Logger logger;

    private Task task;

    private File jobsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode jobsConfig;

    private MinerJob miner;
    private LumberjackJob lumberjack;
    private WarriorJob warrior;
    private FishermanJob fisherman;

    /**
     * Constructor
     *
     * @param totalEconomy object representing this plugin
     */
    public TEJobs(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        //Initialize each job
        miner = new MinerJob();
        lumberjack = new LumberjackJob();
        warrior = new WarriorJob();
        fisherman = new FishermanJob();

        accountManager = totalEconomy.getAccountManager();
        accountConfig = accountManager.getAccountConfig();
        logger = totalEconomy.getLogger();

        jobsFile = new File(totalEconomy.getConfigDir(), "jobs.conf");
        loader = HoconConfigurationLoader.builder().setFile(jobsFile).build();

        try {
            jobsConfig = loader.load();
        } catch (IOException e) {
            jobsConfig = loader.createEmptyNode(ConfigurationOptions.defaults());
        }

        setupConfig();

        if (totalEconomy.isLoadSalary())
            startSalaryTask();
    }

    private void startSalaryTask() {
        Scheduler scheduler = totalEconomy.getGame().getScheduler();
        Task.Builder payTask = scheduler.createTaskBuilder();

        task = payTask.execute(() -> {
                for (Player player : totalEconomy.getServer().getOnlinePlayers()) {
                    BigDecimal salary = new BigDecimal(jobsConfig.getNode(getPlayerJob(player), "salary").getFloat());
                    boolean salaryDisabled = jobsConfig.getNode(getPlayerJob(player), "disablesalary").getBoolean();

                    if (!salaryDisabled) {
                        accountManager.addToBalance(player.getUniqueId(), salary, false);
                        player.sendMessage(Texts.of(TextColors.GRAY, "Your salary of ", TextColors.GOLD,
                                totalEconomy.getCurrencySymbol(), salary, TextColors.GRAY, " has just been paid."));
                    }
                }
        }).delay(jobsConfig.getNode("salarydelay").getInt(), TimeUnit.SECONDS).interval(jobsConfig.getNode("salarydelay")
                .getInt(), TimeUnit.SECONDS).name("Pay Day").submit(totalEconomy);
    }

    /**
     * Setup the jobs config
     */
    public void setupConfig() {
        try {
            if (!jobsFile.exists()) {
                jobsFile.createNewFile();

                jobsConfig.getNode("preventJobFarming").setValue(false);
                jobsConfig.getNode("jobs").setValue("Miner, Lumberjack, Warrior, Fisherman");

                miner.setupJobValues(jobsConfig);
                lumberjack.setupJobValues(jobsConfig);
                warrior.setupJobValues(jobsConfig);
                fisherman.setupJobValues(jobsConfig);

                jobsConfig.getNode("Unemployed", "disablesalary").setValue(false);
                jobsConfig.getNode("Unemployed", "salary").setValue(20);
                jobsConfig.getNode("salarydelay").setValue(300);

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
            player.sendMessage(Texts.of(TextColors.GRAY, "You have gained ", TextColors.GOLD, expAmount, TextColors.GRAY,
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
        int playerLevel = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Level").getInt();
        int playerCurExp = accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").getInt();
        int expToLevel = getExpToLevel(player);

        if (playerCurExp >= expToLevel) {
            accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Level").setValue(playerLevel + 1);
            accountConfig.getNode(playerUUID.toString(), "jobstats", jobName + "Exp").setValue(playerCurExp - expToLevel);
            player.sendMessage(Texts.of(TextColors.GRAY, "Congratulations, you are now a level ", TextColors.GOLD,
                    playerLevel + 1, " ", jobName, "."));
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
        boolean jobPermissions = totalEconomy.isJobPermissions();

        if (jobExists(jobName)) {
            if ((jobPermissions && player.hasPermission("main.job." + jobName)) || !jobPermissions) {
                jobName = convertToTitle(jobName);

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
                player.sendMessage(Texts.of(TextColors.RED, "You do not have permission to become this job."));
            }
        } else {
            player.sendMessage(Texts.of(TextColors.RED, "[TEJobs] This job does not exist"));
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
        if (jobsConfig.getNode(convertToTitle(jobName)).getValue() != null) {
            return true;
        }

        return false;
    }

    public String convertToTitle(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    @Listener
    public void onJobSignCheck(ChangeSignEvent event) {
        SignData data = event.getText();
        Text lineOne = data.lines().get(0);
        Text lineTwo = data.lines().get(1);
        String lineOnePlain = Texts.toPlain(lineOne);
        String lineTwoPlain = Texts.toPlain(lineTwo);

        if (lineOnePlain.equals("[TEJobs]")) {
            lineOne = lineOne.builder().color(TextColors.GOLD).build();

            data.set(data.lines().set(0, lineOne));

            if (jobExists(lineTwoPlain)) {
                String jobName = convertToTitle(lineTwoPlain);

                lineTwo = Texts.of(jobName).builder().color(TextColors.GRAY).build();

                data.set(data.lines().set(1, lineTwo));
                data.set(data.lines().set(2, Texts.of()));
                data.set(data.lines().set(3, Texts.of()));
            }
        }
    }

    @Listener
    public void onSignInteract(InteractBlockEvent event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            Optional<TileEntity> tileEntityOpt = event.getTargetBlock().getLocation().get().getTileEntity();

            if (tileEntityOpt.isPresent()) {
                TileEntity tileEntity = tileEntityOpt.get();

                if (tileEntity instanceof Sign) {
                    Sign sign = (Sign) tileEntity;
                    Optional<SignData> data = sign.getOrCreate(SignData.class);

                    if (data.isPresent()) {
                        SignData signData = data.get();
                        String lineOne = Texts.toPlain(signData.lines().get(0));
                        String lineTwo = Texts.toPlain(signData.lines().get(1));

                        if (lineOne.equals("[TEJobs]") && jobExists(lineTwo)) {
                            setJob(player, lineTwo);
                        }
                    }
                }
            }
        }
    }


    /**
     * Begin all the job listeners
     */


    /**
     * Used for the break option in jobs. Will check if the job has the break node and if it does it will check if the
     * block that was broken is present in the config of the player's job. If it is, it will grab the job exp reward as
     * well as the pay.
     *
     * @param event PlayerBlockBreakEvent
     */
    @Listener
    public void onPlayerBlockBreak(ChangeBlockEvent.Break event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();
            String playerJob = getPlayerJob(player);
            String blockName = event.getTransactions().get(0).getOriginal().getState().getType().getName().split(":")[1];
            Location blockLoc = event.getTransactions().get(0).getOriginal().getLocation().get();

            //Checks if the users current job has the break node.
            boolean hasBreakNode = (jobsConfig.getNode(playerJob, "break").getValue() != null);
            boolean preventFarming = jobsConfig.getNode("preventJobFarming").getBoolean();

            if (jobsConfig.getNode(playerJob).getValue() != null) {
                if (hasBreakNode && jobsConfig.getNode(playerJob, "break", blockName).getValue() != null) {
                    if (preventFarming)
                        blockLoc.setBlockType(BlockTypes.AIR);
                    //event.getSourceTransform().getLocation().setBlockType(BlockTypes.AIR);

                    int expAmount = jobsConfig.getNode(playerJob, "break", blockName, "expreward").getInt();
                    BigDecimal payAmount = new BigDecimal(jobsConfig.getNode(playerJob, "break", blockName, "pay").getString()).setScale(2, BigDecimal.ROUND_DOWN);
                    boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                    addExp(player, expAmount);
                    accountManager.addToBalance(playerUUID, payAmount, notify);
                    checkForLevel(player);
                }
            }
        }
    }

    @Listener
    public void onPlayerPlaceBlock(ChangeBlockEvent.Place event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();
            String playerJob = getPlayerJob(player);
            String blockName = event.getTransactions().get(0).getFinal().getState().getType().getName().split(":")[1];

            //Checks if the users current job has the place node.
            boolean hasPlaceNode = (jobsConfig.getNode(playerJob, "place").getValue() != null);

            if (jobsConfig.getNode(playerJob).getValue() != null) {
                if (hasPlaceNode && jobsConfig.getNode(playerJob, "place", blockName).getValue() != null) {
                    int expAmount = jobsConfig.getNode(playerJob, "place", blockName, "expreward").getInt();
                    BigDecimal payAmount = new BigDecimal(jobsConfig.getNode(playerJob, "place", blockName, "pay").getString()).setScale(2, BigDecimal.ROUND_DOWN);
                    boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                    addExp(player, expAmount);
                    accountManager.addToBalance(playerUUID, payAmount, notify);
                    checkForLevel(player);
                }
            }
        }
    }

    @Listener
    public void onPlayerKillEntity(DestructEntityEvent.Death event) {
        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);

        if (optDamageSource.isPresent()) {
            EntityDamageSource damageSource = optDamageSource.get();
            Entity killer = damageSource.getSource();
            Entity victim = event.getTargetEntity();

            if (killer instanceof Player) {
                Player player = (Player) killer;
                UUID playerUUID = player.getUniqueId();
                String playerJob = getPlayerJob(player);
                String victimName = victim.getType().getName();

                boolean hasKillNode = (jobsConfig.getNode(playerJob, "kill").getValue() != null);

                if (jobsConfig.getNode(playerJob).getValue() != null) {
                    if (hasKillNode && jobsConfig.getNode(playerJob, "kill", victimName).getValue() != null) {
                        int expAmount = jobsConfig.getNode(playerJob, "kill", victimName, "expreward").getInt();
                        BigDecimal payAmount = new BigDecimal(jobsConfig.getNode(playerJob, "kill", victimName, "pay").getString()).setScale(2, BigDecimal.ROUND_DOWN);
                        boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                        addExp(player, expAmount);
                        accountManager.addToBalance(playerUUID, payAmount, notify);
                        checkForLevel(player);
                    }
                }
            }
        }
    }

    @Listener
    public void onPlayerFish(FishingEvent.Stop event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Transaction<ItemStackSnapshot> itemTransaction = event.getItemStackTransaction();
            ItemStack itemStack = itemTransaction.getFinal().createStack();
            Player player = event.getCause().first(Player.class).get();
            UUID playerUUID = player.getUniqueId();
            String playerJob = getPlayerJob(player);

            boolean hasCatchNode = (jobsConfig.getNode(playerJob, "catch").getValue() != null);

            if (itemStack.get(FishData.class).isPresent()) {
                if (jobsConfig.getNode(playerJob).getValue() != null) {
                    FishData fishData = itemStack.get(FishData.class).get();
                    String fishName = fishData.type().get().getName();

                    if (hasCatchNode && jobsConfig.getNode(playerJob, "catch", fishName).getValue() != null) {
                        int expAmount = jobsConfig.getNode(playerJob, "catch", fishName, "expreward").getInt();
                        BigDecimal payAmount = new BigDecimal(jobsConfig.getNode(playerJob, "catch", fishName, "pay").getString()).setScale(2, BigDecimal.ROUND_DOWN);
                        boolean notify = accountConfig.getNode(playerUUID.toString(), "jobnotifications").getBoolean();

                        addExp(player, expAmount);
                        accountManager.addToBalance(playerUUID, payAmount, notify);
                        checkForLevel(player);
                    }
                }

            }
        }
    }
}