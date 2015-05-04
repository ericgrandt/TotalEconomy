package com.erigitic.main;

import com.erigitic.commands.BalanceCommand;
import com.erigitic.commands.PayCommand;
import com.erigitic.config.AccountManager;
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
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.io.File;
import java.io.IOException;

@Plugin(id = "TotalEconomy", name = "Total Economy", version = "0.1.0")
public class TotalEconomy {

    @Inject
    private Logger logger;

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

    /**
     * Setup all config files
     *
     * @param event
     */
    @Subscribe
    public void preInit(PreInitializationEvent event) {
        accountManager = new AccountManager(this);

        //Checks for folder and file existence and creates them if need be.
        try {
            if (!defaultConf.getParentFile().exists()) {
                defaultConf.getParentFile().mkdir();

                if (!defaultConf.exists()) {
                    defaultConf.createNewFile();
                    config = configManager.load();

                    config.getNode("version").setValue("0.0.1");
                    configManager.save(config);
                }
            }

            config = configManager.load();
        } catch (IOException e) {
            logger.warn("Error: Default Config could not be loaded/created!");
        }

        accountManager.setupConfig();
    }

    /**
     * Create and register all commands
     *
     * @param event
     */
    @Subscribe
    public void init(InitializationEvent event) {
        createAndRegisterCommands();
    }

    //TODO: Might not need this
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
        Player player = event.getPlayer();

        accountManager.createAccount(player);
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Creates and registers commands
     */
    private void createAndRegisterCommands() {
        CommandSpec payCommand = CommandSpec.builder()
                .setDescription(Texts.of("Pay another player"))
                .setExtendedDescription(Texts.of("Pay another player"))
                .setExecutor(new PayCommand(this))
                .setArguments(GenericArguments.seq(
                        GenericArguments.player(Texts.of("player"), game),
                        GenericArguments.string(Texts.of("amount"))))
                .build();

        CommandSpec balanceCommand = CommandSpec.builder()
                .setDescription(Texts.of("Display your balance"))
                .setExtendedDescription(Texts.of("Display your balance"))
                .setExecutor(new BalanceCommand(this))
                .build();

        game.getCommandDispatcher().register(this, payCommand, "pay");
        game.getCommandDispatcher().register(this, balanceCommand, "balance");
    }
}