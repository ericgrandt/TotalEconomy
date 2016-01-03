package com.erigitic.config;

import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Erigitic on 5/2/2015.
 */
public class AccountManager implements EconomyService {
    private TotalEconomy totalEconomy;
    private Logger logger;
    private File accountsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode accountConfig;
    private Server server;

    /**
     * Default Constructor so we can access this class from elsewhere
     */
    public AccountManager(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();
        server = totalEconomy.getServer();

        setupConfig();
    }

    /**
     * Setup the config file that will contain the user accounts. These accounts will contain the users money amount.
     */
    public void setupConfig() {
        accountsFile = new File(totalEconomy.getConfigDir(), "accounts.conf");
        loader = HoconConfigurationLoader.builder().setFile(accountsFile).build();

        try {
            accountConfig = loader.load();

            if (!accountsFile.exists()) {
                loader.save(accountConfig);
            }
        } catch (IOException e) {
            logger.warn("Could not create accounts config file!");
        }
    }


    /**
     * Creates a new account for the player.
     *
     * @param uuid object representing the UUID of a player
     */
    @Override
    public Optional<UniqueAccount> createAccount(UUID uuid) {
        String currencyName = getDefaultCurrency().getDisplayName().toPlain().toLowerCase();

        try {
            if (accountConfig.getNode(uuid.toString(), currencyName + "-balance").getValue() == null) {
                TEAccount playerAccount = new TEAccount(totalEconomy, this, uuid);

                accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(playerAccount.getDefaultBalance(getDefaultCurrency()));
                accountConfig.getNode(uuid.toString(), "job").setValue("Unemployed");
                accountConfig.getNode(uuid.toString(), "jobnotifications").setValue("true");

                loader.save(accountConfig);

                return Optional.of(playerAccount);
            }
        } catch (IOException e) {
            logger.warn("Could not create account!");
        }

        return Optional.empty();
    }

    //TODO: Implement later. Some cool things can be done with this.
    @Override
    public Optional<VirtualAccount> createVirtualAccount(String identifier) {
        return Optional.empty();
    }

    @Override
    public Optional<UniqueAccount> getAccount(UUID uuid) {
        String currencyName = getDefaultCurrency().getDisplayName().toPlain().toLowerCase();

        if (accountConfig.getNode(uuid.toString(), currencyName + "-balance").getValue() != null) {
            TEAccount playerAccount = new TEAccount(totalEconomy, this, uuid);

            return Optional.of(playerAccount);
        } else {
            return createAccount(uuid);
        }
    }

    //TODO: Implement later. For virtual accounts.
    @Override
    public Optional<Account> getAccount(String string) {
        return Optional.empty();
    }

    @Override
    public Currency getDefaultCurrency() {
        return totalEconomy.getDefaultCurrency();
    }

    //TODO: Possibly implement multiple currencies. Need some input on it. Up to the users.
    @Override
    public Set<Currency> getCurrencies() {
        return new HashSet<Currency>();
    }

    //TODO: Figure out what this does. Currently have no idea. Let's hope it does not break something.
    @Override
    public void registerContextCalculator(ContextCalculator calculator) {

    }

    public void toggleNotifications(Player player) {
        boolean notify = accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").getBoolean();

        if (notify == true) {
            accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue(false);
            notify = false;
        } else {
            accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue(true);
            notify = true;
        }

        try {
            loader.save(accountConfig);

            if (notify == true)
                player.sendMessage(Text.of(TextColors.GRAY, "Notifications are now ", TextColors.GREEN, "ON"));
            else
                player.sendMessage(Text.of(TextColors.GRAY, "Notifications are now ", TextColors.RED, "OFF"));
        } catch (IOException e) {
            player.sendMessage(Text.of(TextColors.RED, "Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
            logger.warn("Could not save notification change!");
        }
    }

    public void saveAccountConfig() {
        try {
            loader.save(accountConfig);
        } catch (IOException e) {
            logger.error("Could not save the account configuration file!");
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
        return loader;
    }

}
