package com.erigitic.main;

import com.erigitic.config.AccountManager;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;

import java.io.File;
import java.io.IOException;

@Plugin(id = "TotalEconomy", name = "Total Economy", version = "0.0.1")
public class TotalEconomy {

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConf;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private ConfigurationNode config = null;

    private AccountManager accountManager;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        accountManager = new AccountManager();

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
            logger.warn("Default Config could not be loaded/created!");
        }

        accountManager.setupConfig();

        logger.info("Total Economy Started");
    }

    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {
        logger.info("Total Economy Stopped");
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        accountManager.createAccount(event.getPlayer());
    }
}