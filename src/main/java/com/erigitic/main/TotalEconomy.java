package com.erigitic.main;

import com.erigitic.commands.*;
import com.erigitic.config.AccountManager;
import com.erigitic.config.TECurrency;
import com.erigitic.jobs.TEJobs;
import com.erigitic.shops.ShopKeeper;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;

@Plugin(id = "totaleconomy", name = "Total Economy", version = "1.5.0", description = "All in one economy plugin for Minecraft/Sponge")
public class TotalEconomy {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConf;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    private Game game;

    private ConfigurationNode config = null;

    private Currency defaultCurrency;

    private AccountManager accountManager;
    private TEJobs teJobs;
    private ShopKeeper shopKeeper;

    private boolean loadJobs = true;
    private boolean loadSalary = true;
    private boolean jobPermissions = false;
    private boolean jobNotifications = true;

    private boolean loadShopKeeper = true;
    private boolean loadMoneyCap = false;

    private BigDecimal moneyCap;

    /**
     * Setup all config files
     *
     * @param event
     */
    @Listener
    public void preInit(GamePreInitializationEvent event) {
        setupConfig();

        defaultCurrency = new TECurrency(Text.of(config.getNode("currency-singular").getValue()),
                Text.of(config.getNode("currency-plural").getValue()), Text.of(config.getNode("symbol").getValue()), 2, true);

        loadJobs = config.getNode("features", "jobs", "enable").getBoolean();
        loadSalary = config.getNode("features", "jobs", "salary").getBoolean();
        jobPermissions = config.getNode("features", "jobs", "permissions").getBoolean();
        jobNotifications = config.getNode("features", "jobs", "notifications").getBoolean();

        loadShopKeeper = config.getNode("features", "shopkeeper").getBoolean();
        loadMoneyCap = config.getNode("features", "moneycap", "enable").getBoolean();

        accountManager = new AccountManager(this);

        game.getServiceManager().setProvider(this, EconomyService.class, accountManager);

        //Only setup job stuff if config is set to load jobs
        if (loadJobs == true) {
            teJobs = new TEJobs(this);
        }

        if (loadShopKeeper == true) {
            shopKeeper = new ShopKeeper(this);
        }

        if (loadMoneyCap == true) {
            moneyCap = BigDecimal.valueOf(config.getNode("features", "moneycap", "amount").getFloat())
                    .setScale(2, BigDecimal.ROUND_DOWN);
        }
    }

    /**
     * Create and register all commands.
     *
     * @param event
     */
    @Listener
    public void init(GameInitializationEvent event) {
        createAndRegisterCommands();

        if (loadJobs)
            game.getEventManager().registerListeners(this, teJobs);
    }

    @Listener
    public void postInit(GamePostInitializationEvent event) {

    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Total Economy Started");
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        logger.info("Total Economy Stopped");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = event.getTargetEntity();

            accountManager.getOrCreateAccount(player.getUniqueId());
        }
    }

    /**
     * Reloads configuration files
     *
     * @param event
     */
    @Listener
    public void onGameReload(GameReloadEvent event) {
        teJobs.reloadConfig();
    }

    /**
     * Setup the default config file, TotalEconomy.conf.
     */
    private void setupConfig() {
        try {
            config = loader.load();

            if (!defaultConf.exists()) {
                config.getNode("features", "jobs", "enable").setValue(loadJobs);
                config.getNode("features", "jobs", "salary").setValue(loadSalary);
                config.getNode("features", "jobs", "permissions").setValue(jobPermissions);
                config.getNode("features", "jobs", "notifications").setValue(true);
                config.getNode("features", "moneycap", "enable").setValue(loadMoneyCap);
                config.getNode("features", "moneycap", "amount").setValue(10000000);
                config.getNode("features", "shopkeeper").setValue(loadShopKeeper);
                config.getNode("startbalance").setValue(100);
                config.getNode("currency-singular").setValue("Dollar");
                config.getNode("currency-plural").setValue("Dollars");
                config.getNode("symbol").setValue("$");
                loader.save(config);
            }

            // TODO: Make this into its own function that will update ALL values
            // Change job notifaction state for pre existing configuration files
            ConfigurationNode jobNotificationState = config.getNode("features", "jobs", "notifications");
            if (jobNotificationState.isVirtual()) {
                jobNotificationState.setValue(true);

                loader.save(config);
            }

        } catch (IOException e) {
            logger.warn("Main config could not be loaded/created/changed!");
        }
    }

    /**
     * Creates and registers commands
     */
    private void createAndRegisterCommands() {
        CommandSpec payCommand = CommandSpec.builder()
                .description(Text.of("Pay another player"))
                .permission("totaleconomy.command.pay")
                .executor(new PayCommand(this))
                .arguments(GenericArguments.player(Text.of("player")),
                        GenericArguments.string(Text.of("amount")))
                .build();

        CommandSpec adminPayCommand = CommandSpec.builder()
                .description(Text.of("Pay a player without removing money from your balance."))
                .permission("totaleconomy.command.adminpay")
                .executor(new AdminPayCommand(this))
                .arguments(GenericArguments.player(Text.of("player")),
                        GenericArguments.string(Text.of("amount")))
                .build();

        CommandSpec balanceCommand = CommandSpec.builder()
                .description(Text.of("Display your balance"))
                .permission("totaleconomy.command.balance")
                .executor(new BalanceCommand(this))
                .build();

        CommandSpec balanceTopCommand = CommandSpec.builder()
                .description(Text.of("Display top balances"))
                .permission("totaleconomy.command.balancetop")
                .executor(new BalanceTopCommand(this))
                .build();

        CommandSpec viewBalanceCommand = CommandSpec.builder()
                .description(Text.of("View the balance of another player"))
                .permission("totaleconomy.command.viewbalance")
                .executor(new ViewBalanceCommand(this))
                .arguments(GenericArguments.user(Text.of("player")))
                .build();

        CommandSpec setBalanceCommand = CommandSpec.builder()
                .description(Text.of("Set a player's balance"))
                .permission("totaleconomy.command.setbalance")
                .executor(new SetBalanceCommand(this))
                .arguments(GenericArguments.player(Text.of("player")),
                        GenericArguments.string(Text.of("amount")))
                .build();

        //Only enables job commands if the value for jobs in config is set to true
        if (loadJobs == true) {
            CommandSpec jobSetCmd = CommandSpec.builder()
                    .description(Text.of("Set your job"))
                    .permission("totaleconomy.command.jobset")
                    .executor(new JobCommand(this))
                    .arguments(GenericArguments.string(Text.of("jobName")))
                    .build();

            CommandSpec jobNotifyToggle = CommandSpec.builder()
                    .description(Text.of("Toggle job notifications on/off"))
                    .permission("totaleconomy.command.jobtoggle")
                    .executor(new JobToggleCommand(this))
                    .build();

            //TODO: Implement later?
            CommandSpec jobInfoCmd = CommandSpec.builder()
                    .description(Text.of("Prints out a list of items that reward exp and money for the current job"))
                    .permission("totaleconomy.command.jobinfo")
                    .executor(new JobInfoCommand(this))
                    .build();

            CommandSpec jobCommand = CommandSpec.builder()
                    .description(Text.of("Display list of jobs."))
                    .permission("totaleconomy.command.job")
                    .executor(new JobCommand(this))
                    .child(jobSetCmd, "set", "s")
                    .child(jobNotifyToggle, "toggle", "t")
                    .child(jobInfoCmd, "info", "i")
                    .build();


            game.getCommandManager().register(this, jobCommand, "job");
        }

        game.getCommandManager().register(this, payCommand, "pay");
        game.getCommandManager().register(this, adminPayCommand, "adminpay");
        game.getCommandManager().register(this, balanceCommand, "balance", "bal");
        game.getCommandManager().register(this, viewBalanceCommand, "viewbalance", "vbal");
        game.getCommandManager().register(this, setBalanceCommand, "setbalance", "setbal");
        game.getCommandManager().register(this, balanceTopCommand, "balancetop", "baltop");
    }

    /**
     * Determines if the String passed in is numeric or not
     *
     * @param str the String to check
     *
     * @return boolean whether or not the String is numeric
     */
    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public TEJobs getTEJobs() {
        return teJobs;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getConfigDir() {
        return configDir;
    }

    public BigDecimal getStartingBalance() { return new BigDecimal(config.getNode("startbalance").getString()); }

    public String getCurrencySymbol() {
        return config.getNode("symbol").getValue().toString();
    }

    public Server getServer() {
        return game.getServer();
    }

    public Game getGame() { return game; }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public boolean isLoadSalary() {
        return loadSalary;
    }

    public boolean isJobPermissions() { return jobPermissions; }

    public boolean isLoadMoneyCap() {
        return loadMoneyCap;
    }

    public BigDecimal getMoneyCap() {
        return moneyCap.setScale(2, BigDecimal.ROUND_DOWN);
    }

    public boolean hasJobNotifications() { return jobNotifications; }

}