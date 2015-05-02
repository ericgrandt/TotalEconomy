package com.erigitic.config;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.service.config.DefaultConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created by Erigitic on 5/2/2015.
 */
public class AccountManager {

    @Inject
    private Logger logger;

    //Load the default config so we grab the parent directory.

    private File accountsConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    /**
     * Default Constructor so we can access this class from elsewhere
     */
    public AccountManager() {

    }

    /**
     * Setup the config file that will contain the user accounts. These accounts will contain the users money amount.
     */
    public void setupConfig() {
        accountsConfig = new File("config/TotalEconomy/accounts.conf");

        try {
            if (!accountsConfig.exists()) {
                accountsConfig.createNewFile();
            }
        } catch (IOException e) {
            logger.warn("ERROR: Could not load/create accounts config file!");
        }
    }
}
