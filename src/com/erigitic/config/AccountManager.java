package com.erigitic.config;

import com.erigitic.main.TotalEconomy;
import com.erigitic.service.TEService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Erigitic on 5/2/2015.
 */
public class AccountManager implements TEService {
    private TotalEconomy totalEconomy;
    private Logger logger;
    private File accountsFile;
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private ConfigurationNode accountConfig;

    /**
     * Default Constructor so we can access this class from elsewhere
     */
    public AccountManager(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountsFile = new File(totalEconomy.getConfigDir(), "accounts.conf");
        configManager = HoconConfigurationLoader.builder().setFile(accountsFile).build();

        try {
            accountConfig = configManager.load();
        } catch (IOException e) {
            logger.warn("Could not load account config!");
        }
    }

    /**
     * Setup the config file that will contain the user accounts. These accounts will contain the users money amount.
     */
    public void setupConfig() {
        try {
            if (!accountsFile.exists()) {
                accountsFile.createNewFile();
            }
        } catch (IOException e) {
            logger.warn("Could not create accounts config file!");
        }
    }

    /**
     * Creates a new account for the player.
     *
     * @param player object representing a Player
     */
    public void createAccount(Player player) {
        try {
            if (accountConfig.getNode(player.getUniqueId().toString(), "balance").getValue() == null) {
                //TODO: Set balance to the default config defined starting balance
                BigDecimal startBalance = new BigDecimal("10.00");
                accountConfig.getNode(player.getUniqueId().toString(), "balance").setValue(startBalance.setScale(2, BigDecimal.ROUND_DOWN).toString());
                accountConfig.getNode(player.getUniqueId().toString(), "job").setValue("Unemployed");
                accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue("true");
            }

            configManager.save(accountConfig);
        } catch (IOException e) {
            logger.warn("Could not create account!");
        }
    }

    public void setBalance(Player player, BigDecimal amount) {
        try {
            accountConfig.getNode(player.getUniqueId().toString(), "balance").setValue(amount).toString();
            configManager.save(accountConfig);
        } catch (IOException e) {
            logger.warn("Could not set player balance!");
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
        if (accountConfig.getNode(player.getUniqueId().toString()) != null)
            return true;
        else
            createAccount(player);

        return false;
    }

    /**
     * Add currency to player's balance.
     *
     * @param player object representing a Player
     * @param amount amount to be added to balance
     */
    public void addToBalance(Player player, BigDecimal amount, boolean notify) {
        BigDecimal newBalance = new BigDecimal(getStringBalance(player)).add(new BigDecimal(amount.toString()));

        if (hasAccount(player)) {
            try {
                accountConfig.getNode(player.getUniqueId().toString(), "balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN).toString());
                configManager.save(accountConfig);

                if (notify)
                    player.sendMessage(Texts.of(TextColors.GOLD, totalEconomy.getCurrencySymbol(), amount, TextColors.GRAY, " has been added to your balance."));
            } catch (IOException e) {
                logger.warn("Could not add to player balance!");
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
                accountConfig.getNode(player.getUniqueId().toString(), "balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN).toString());
                configManager.save(accountConfig);

                player.sendMessage(Texts.of(TextColors.GOLD, totalEconomy.getCurrencySymbol(), amount, TextColors.GRAY, " has been removed from your balance."));
            } catch (IOException e) {
                logger.warn("Could not add to player balance!");
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
            balance = new BigDecimal(accountConfig.getNode(player.getUniqueId().toString(), "balance").getString());
        }

        return balance.setScale(2, BigDecimal.ROUND_DOWN);
    }

    //MAY NOT BE NEEDED
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
            balance = new BigDecimal(accountConfig.getNode(player.getUniqueId().toString(), "balance").getString());
        }

        return balance.setScale(2, BigDecimal.ROUND_DOWN).toString();
    }

    public void toggleNotifications(Player player) {
        boolean notify = accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").getBoolean();

        if (notify == true) {
            accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue("false");
            notify = false;
        } else {
            accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue("true");
            notify = true;
        }

        try {
            configManager.save(accountConfig);

            if (notify == true)
                player.sendMessage(Texts.of(TextColors.GRAY, "Notifications are now ", TextColors.GREEN, "ON"));
            else
                player.sendMessage(Texts.of(TextColors.GRAY, "Notifications are now ", TextColors.RED, "OFF"));
        } catch (IOException e) {
            player.sendMessage(Texts.of(TextColors.RED, "Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
            logger.warn("Could not save notification change!");
        }
    }

    /**
     * Get the account configuration file
     *
     * @return ConfigurationNode
     */
    public ConfigurationNode getAccountConfig() {
        return accountConfig;
    }

    /**
     * Get the configuration manager
     *
     * @return ConfigurationLoader<CommentedConfigurationNode>
     */
    public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
        return configManager;
    }

}
