package com.erigitic.config;

import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Erigitic on 5/2/2015.
 */
public class AccountManager {
    private TotalEconomy totalEconomy;
    private Logger logger;
    private File accountsConfig;
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private ConfigurationNode config = null;

    /**
     * Default Constructor so we can access this class from elsewhere
     */
    public AccountManager(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

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
     * @param player object representing a Player
     */
    public void createAccount(Player player) {
        try {
            config = configManager.load();

            if (config.getNode(player.getName(), "balance").getValue() == null) {
                //TODO: Set balance to the default config defined starting balance
                BigDecimal startBalance = new BigDecimal("10.00");
                config.getNode(player.getName(), "balance").setValue(startBalance.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString());
            }

            configManager.save(config);
        } catch (IOException e) {
            logger.warn("Error: Could not create account!");
        }
    }

    /**
     * Checks if the specified player has an account. If not, one will be created.
     *
     * @param player object representing a Player
     *
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
     * Add currency to player's balance.
     *
     * @param player object representing a Player
     * @param amount amount to be added to balance
     */
    public void addToBalance(Player player, BigDecimal amount) {
        BigDecimal newBalance = new BigDecimal(getStringBalance(player)).add(new BigDecimal(amount.toString()));

        if (hasAccount(player)) {
            try {
                config = configManager.load();

                config.getNode(player.getName(), "balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString());
                configManager.save(config);

                player.sendMessage(Texts.of(amount + " has been added to your balance!"));
            } catch (IOException e) {
                logger.warn("Error: Could not add to player balance!");
            }
        }
    }

    /**
     * Removes an amount from the specified player's balance. Checks if the player has a balance greater then or equal to
     * the amount being removed.
     *
     * @param player object representing a Player
     * @param amount amount to be removed from balance
     */
    public void removeFromBalance(Player player, BigDecimal amount) {
        if (hasAccount(player)) {
            BigDecimal newBalance = new BigDecimal(getStringBalance(player)).subtract(new BigDecimal(amount.toString()));

            try {
                config = configManager.load();

                config.getNode(player.getName(), "balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString());
                configManager.save(config);
            } catch (IOException e) {
                logger.warn("Error: Could not add to player balance!");
            }
        }
    }

    /**
     * Checks if the player has enough money in there balance to remove from.
     *
     * @param player object representing a Player
     * @param amount amount to be checked
     *
     * @return boolean weather or not the player has enough money in balance
     */
    public boolean hasMoney(Player player, BigDecimal amount) {
        BigDecimal balance = getBalance(player);

        int result = amount.compareTo(balance);

        logger.info("" + result);

        if (result == -1 || result == 0)
            return true;
        else
            player.sendMessage(Texts.of("Insufficient funds."));

        return false;
    }

    /**
     * Get the balance for the specified player.
     *
     * @param player object representing a Player
     *
     * @return BigDecimal the balance
     */
    public BigDecimal getBalance(Player player) {
        BigDecimal balance = new BigDecimal(0);

        if (hasAccount(player)) {
            try {
                config = configManager.load();

                balance = new BigDecimal((String) config.getNode(player.getName(), "balance").getValue());
            } catch (IOException e) {
                logger.warn("Error: Could not get player balance from config!");
            }
        }

        return balance.setScale(2, BigDecimal.ROUND_UNNECESSARY);
    }

    /**
     * Get the balance in string form in order to more easily print in game.
     *
     * @param player object representing a Player
     *
     * @return String represents the balance in string form
     */
    public String getStringBalance(Player player) {
        BigDecimal balance = new BigDecimal(0);

        if (hasAccount(player)) {
            try {
                config = configManager.load();

                balance = new BigDecimal((String) config.getNode(player.getName(), "balance").getValue());
            } catch (IOException e) {
                logger.warn("Error: Could not get player balance from config!");
            }
        }

        return balance.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
    }

}
