package com.erigitic.main;

import com.erigitic.commands.BalanceCommand;
import com.erigitic.commands.JobCommand;
import com.erigitic.commands.PayCommand;
import com.erigitic.config.AccountManager;
import com.erigitic.jobs.TEJobs;
import com.erigitic.service.TEService;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.*;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.io.File;
import java.io.IOException;

@Plugin(id = "TotalEconomy", name = "Total Economy", version = "1.0.0")
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
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    @Inject
    private Game game;

    private ConfigurationNode config = null;

    private AccountManager accountManager;
    private TEJobs teJobs;

    private boolean loadJobs = true;

    /**
     * Setup all config files
     *
     * @param event
     */
    @Subscribe
    public void preInit(PreInitializationEvent event) {
        setupConfig();

        loadJobs = config.getNode("features", "jobs").getBoolean();

        accountManager = new AccountManager(this);
        accountManager.setupConfig();

        //Only setup job stuff if config is set to load jobs
        if (loadJobs == true) {
            teJobs = new TEJobs(this);
            teJobs.setupConfig();
        }
    }

    /**
     * Create and register all commands.
     *
     * @param event
     */
    @Subscribe
    public void init(InitializationEvent event) {
        createAndRegisterCommands();

        if (!game.getServiceManager().provide(TEService.class).isPresent()) {
            try {
                game.getServiceManager().setProvider(this, TEService.class, new AccountManager(this));
            } catch (ProviderExistsException e) {
                logger.warn("Provider does not exist!");
            }
        }

        game.getEventManager().register(this, teJobs);
    }

    @Subscribe
    public void postInit(PostInitializationEvent event) {

    }

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        logger.info("Total Economy Started");
    }

    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {
        logger.info("Total Economy Stopped");
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getUser();

        accountManager.createAccount(player);
    }

    /**
     * Setup the default config file, TotalEconomy.conf.
     */
    private void setupConfig() {
        try {
            if (!defaultConf.getParentFile().exists()) {
                defaultConf.getParentFile().mkdir();
            }

            if (!defaultConf.exists()) {
                defaultConf.createNewFile();
                config = configManager.load();

                config.getNode("features", "jobs").setValue(true);
                config.getNode("symbol").setValue("$");
                configManager.save(config);
            }
            config = configManager.load();
        } catch (IOException e) {
            logger.warn("Default Config could not be loaded/created!");
        }
    }

    /**
     * Creates and registers commands
     */
    private void createAndRegisterCommands() {
        //TODO: Add command that will display all of the enabled features. Maybe even disabled features?

        CommandSpec payCommand = CommandSpec.builder()
                .description(Texts.of("Pay another player"))
                .executor(new PayCommand(this))
                .arguments(GenericArguments.seq(
                        GenericArguments.player(Texts.of("player"), game),
                        GenericArguments.string(Texts.of("amount"))))
                .build();

        CommandSpec balanceCommand = CommandSpec.builder()
                .description(Texts.of("Display your balance"))
                .executor(new BalanceCommand(this))
                .build();

        //Only enables job commands if the value for jobs in config is set to true
        if (loadJobs == true) {
            CommandSpec jobCommand = CommandSpec.builder()
                    .description(Texts.of("Display list of jobs."))
                    .executor(new JobCommand(this))
                    .build();

            CommandSpec jobSetCommand = CommandSpec.builder()
                    .description(Texts.of("Set your job"))
                    .executor(new JobCommand(this))
                    .arguments(GenericArguments.seq(
                            GenericArguments.string(Texts.of("jobName"))))
                    .build();

            game.getCommandDispatcher().register(this, jobCommand, "job");
            game.getCommandDispatcher().register(this, jobSetCommand, "jobset");
        }

        game.getCommandDispatcher().register(this, payCommand, "pay");
        game.getCommandDispatcher().register(this, balanceCommand, "balance");
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

    public String getCurrencySymbol() {
        return config.getNode("symbol").getValue().toString();
    }
}