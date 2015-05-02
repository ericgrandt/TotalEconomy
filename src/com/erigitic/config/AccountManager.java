package com.erigitic.config;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
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
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private ConfigurationNode config = null;

    /**
     * Default Constructor so we can access this class from elsewhere
     */
    public AccountManager() {
        accountsConfig = new File("config/TotalEconomy/accounts.conf");
        configManager = HoconConfigurationLoader.builder().setFile(accountsConfig).build();
    }

    /**
     * Setup the config file that will contain the user accounts. These accounts will contain the users money amount.
     */
    public void setupConfig() {
        try {
            if (!accountsConfig.exists()) {
                accountsConfig.createNewFile();
            }
        } catch (IOException e) {
            logger.warn("Error: Could not load/create accounts config file!");
        }
    }

    /**
     * Creates a new account for the player.
     *
     * @param player Object representing a Player
     */
    public void createAccount(Player player) {
        try {
            config = configManager.load();

            if (config.getNode(player.getName(), "balance").getValue() == null) {
                //TODO: Set balance to the default config defined starting balance
                config.getNode(player.getName(), "balance").setValue("0");
            }

            configManager.save(config);
        } catch (IOException e) {
            logger.warn("Error: Could not create account!");
        }
    }

    /**
     * Checks if the specified player has an account. If not, one will be created.
     *
     * @param player Object representing a Player
     * @return weather or not the player has an account
     */
    public boolean hasAccount(Player player) {
        try {
            config = configManager.load();

            if (config.getNode(player.getName()).getValue() != null)
                return true;
            else
                createAccount(player);
        } catch (IOException e) {
            logger.warn("Error: Could not determine if player has an account or not!");
        }

        return false;
    }

    /**
     * Get the balance for the specified player.
     *
     * @param player Object representing a Player
     * @return
     */
    public float getBalance(Player player) {
        float balance = 0;

        if (hasAccount(player)) {
            try {
                config = configManager.load();

                balance = (float) config.getNode(player.getName(), "balance").getValue();
            } catch (IOException e) {
                logger.warn("Error: Could not get player balance from config!");
            }
        }

        return balance;
    }

}
