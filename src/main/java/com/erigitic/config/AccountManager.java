/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.config;

import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.SQLHandler;
import com.erigitic.sql.SQLQuery;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AccountManager implements EconomyService {
    private TotalEconomy totalEconomy;
    private Logger logger;
    private File accountsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode accountConfig;

    private SQLHandler sqlHandler;

    private boolean databaseActive;

    private boolean confSaveRequested = false;

    /**
     * Constructor for the AccountManager class. Handles the initialization of necessary variables, setup of the database
     * or configuration files depending on main configuration value, and starts save script if setup.
     *
     * @param totalEconomy Main plugin class
     */
    public AccountManager(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();
        databaseActive = totalEconomy.isDatabaseActive();

        if (databaseActive) {
            sqlHandler = totalEconomy.getSqlHandler();

            setupDatabase();
        } else {
            setupConfig();

            if (totalEconomy.getSaveInterval() > 0) {
                Sponge.getScheduler().createTaskBuilder().interval(totalEconomy.getSaveInterval(), TimeUnit.SECONDS)
                        .execute(() -> {
                            writeToDisk(false);
                        }).submit(totalEconomy);
            }
        }
    }

    /**
     * Setup the config file that will contain the user accounts
     */
    private void setupConfig() {
        accountsFile = new File(totalEconomy.getConfigDir(), "accounts.conf");
        loader = HoconConfigurationLoader.builder().setFile(accountsFile).build();

        try {
            accountConfig = loader.load();

            if (!accountsFile.exists()) {
                loader.save(accountConfig);
            }
        } catch (IOException e) {
            logger.warn("[TE] Error creating accounts configuration file!");
        }
    }

    /**
     * Setup the database that will contain the user accounts
     */
    public void setupDatabase() {
        String currencyCols = "";

        for (Currency currency : getCurrencies()) {
            TECurrency teCurrency = (TECurrency) currency;

            currencyCols += teCurrency.getName().toLowerCase() + "_balance decimal(19,2) NOT NULL DEFAULT '" + teCurrency.getStartingBalance() + "',";
        }

        sqlHandler.createTable("accounts", "uid varchar(60) NOT NULL," +
                currencyCols +
                "job varchar(50) NOT NULL DEFAULT 'Unemployed'," +
                "job_notifications boolean NOT NULL DEFAULT TRUE," +
                "PRIMARY KEY (uid)");

        sqlHandler.createTable("virtual_accounts", "uid varchar(60) NOT NULL," +
                getDefaultCurrency().getName().toLowerCase() + "_balance decimal(19,2) NOT NULL DEFAULT '" + totalEconomy.getDefaultCurrency().getStartingBalance() + "'," +
                "PRIMARY KEY (uid)");

        sqlHandler.createTable("levels", "uid varchar(60)," +
                "miner int(10) unsigned NOT NULL DEFAULT '1'," +
                "lumberjack int(10) unsigned NOT NULL DEFAULT '1'," +
                "warrior int(10) unsigned NOT NULL DEFAULT '1'," +
                "fisherman int(10) unsigned NOT NULL DEFAULT '1'," +
                "FOREIGN KEY (uid) REFERENCES accounts(uid) ON DELETE CASCADE");

        sqlHandler.createTable("experience", "uid varchar(60)," +
                "miner int(10) unsigned NOT NULL DEFAULT '0'," +
                "lumberjack int(10) unsigned NOT NULL DEFAULT '0'," +
                "warrior int(10) unsigned NOT NULL DEFAULT '0'," +
                "fisherman int(10) unsigned NOT NULL DEFAULT '0'," +
                "FOREIGN KEY (uid) REFERENCES accounts(uid) ON DELETE CASCADE");
    }

    /**
     * Reload the account config
     */
    public void reloadConfig() {
        try {
            accountConfig = loader.load();
            logger.info("[TE] Reloading account configuration file.");
        } catch (IOException e) {
            logger.warn("[TE] An error occurred while reloading the account configuration file!");
        }
    }

    /**
     * Gets or creates a unique account for the passed in UUID
     *
     * @param uuid {@link UUID} of the player an account is being created for
     * @return Optional<UniqueAccount> The account that was retrieved or created
     */
    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        TEAccount playerAccount = new TEAccount(totalEconomy, this, uuid);

        try {
            if (!hasAccount(uuid)) {
                if (databaseActive) {
                    SQLQuery.builder(sqlHandler.dataSource).insert("accounts")
                            .columns("uid", "job", "job_notifications")
                            .values(uuid.toString(), "unemployed", String.valueOf(totalEconomy.hasJobNotifications()))
                            .build();

                    SQLQuery.builder(sqlHandler.dataSource).insert("levels")
                            .columns("uid")
                            .values(uuid.toString())
                            .build();

                    SQLQuery.builder(sqlHandler.dataSource).insert("experience")
                            .columns("uid")
                            .values(uuid.toString())
                            .build();

                    for (Currency currency : totalEconomy.getCurrencies()) {
                        TECurrency teCurrency = (TECurrency) currency;

                        SQLQuery.builder(sqlHandler.dataSource).update("accounts")
                                .set(teCurrency.getName().toLowerCase() + "_balance")
                                .equals(playerAccount.getDefaultBalance(teCurrency).toString())
                                .where("uid")
                                .equals(uuid.toString())
                                .build();
                    }
                } else {
                    for (Currency currency : totalEconomy.getCurrencies()) {
                        TECurrency teCurrency = (TECurrency) currency;

                        accountConfig.getNode(uuid.toString(), teCurrency.getName().toLowerCase() + "-balance").setValue(playerAccount.getDefaultBalance(teCurrency));
                    }

                    accountConfig.getNode(uuid.toString(), "job").setValue("unemployed");
                    accountConfig.getNode(uuid.toString(), "jobnotifications").setValue(totalEconomy.hasJobNotifications());
                    loader.save(accountConfig);
                }
            }
        } catch (IOException e) {
            logger.warn("[TE] An error occurred while creating a new account!");
        }

        return Optional.of(playerAccount);
    }

    /**
     * Gets or creates a virtual account for the passed in identifier
     *
     * @param identifier The virtual accounts identifier
     * @return Optional<Account> The virtual account that was retrieved or created
     */
    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        String currencyName = getDefaultCurrency().getDisplayName().toPlain().toLowerCase();
        TEVirtualAccount virtualAccount = new TEVirtualAccount(totalEconomy, this, identifier);

        try {
            if (!hasAccount(identifier)) {
                if (databaseActive) {
                    SQLQuery.builder(sqlHandler.dataSource).insert("virtual_accounts")
                            .columns("uid", currencyName + "_balance")
                            .values(identifier, virtualAccount.getDefaultBalance(getDefaultCurrency()).toString())
                            .build();
                } else {
                    accountConfig.getNode(identifier, currencyName + "-balance").setValue(virtualAccount.getDefaultBalance(getDefaultCurrency()));
                    loader.save(accountConfig);
                }
            }
        } catch (IOException e) {
            logger.warn("[TE] An error occurred while creating a new virtual account!");
        }

        return Optional.of(virtualAccount);
    }

    /**
     * Determines if a unique account is associated with the passed in UUID
     *
     * @param uuid {@link UUID} to check for an account
     * @return boolean Whether or not an account is associated with the passed in UUID
     */
    @Override
    public boolean hasAccount(UUID uuid) {
        if (databaseActive) {
            SQLQuery query = SQLQuery.builder(sqlHandler.dataSource)
                    .select("uid")
                    .from("accounts")
                    .where("uid")
                    .equals(uuid.toString())
                    .build();

            return query.recordExists();
        } else {
            return accountConfig.getNode(uuid.toString()).getValue() != null;
        }
    }

    /**
     * Determines if a virtual account is associated with the passed in UUID
     *
     * @param identifier The identifier to check for an account
     * @return boolean Whether or not a virtual account is associated with the passed in identifier
     */
    @Override
    public boolean hasAccount(String identifier) {
        if (databaseActive) {
            SQLQuery query = SQLQuery.builder(sqlHandler.dataSource)
                    .select("uid")
                    .from("virtual_accounts")
                    .where("uid")
                    .equals(identifier)
                    .build();

            return query.recordExists();
        } else {
            return accountConfig.getNode(identifier).getValue() != null;
        }
    }

    /**
     * Gets the default {@link Currency}
     *
     * @return Currency The default currency
     */
    @Override
    public Currency getDefaultCurrency() {
        return totalEconomy.getDefaultCurrency();
    }

    /**
     * Gets a set containing all of the currencies
     *
     * @return Set<Currency> Set of all currencies
     */
    @Override
    public Set<Currency> getCurrencies() {
        return totalEconomy.getCurrencies();
    }

    @Override
    public void registerContextCalculator(ContextCalculator calculator) {

    }

    /**
     * Gets the passed in player's notification state
     *
     * @param player The {@link Player} who's notification state to get
     * @return boolean The notification state
     */
    public boolean getJobNotificationState(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (databaseActive) {
            SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource).select("job_notifications")
                    .from("accounts")
                    .where("uid")
                    .equals(playerUUID.toString())
                    .build();

            return sqlQuery.getBoolean(true);
        } else {
            return accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").getBoolean(true);
        }
    }

    /**
     * Toggle a player's exp/money notifications for jobs
     *
     * @param player Player toggling notifications
     */
    public void toggleNotifications(Player player) {
        boolean jobNotifications = !getJobNotificationState(player);
        UUID playerUUID = player.getUniqueId();

        if (databaseActive) {
            SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource).update("accounts")
                    .set("job_notifications")
                    .equals(jobNotifications ? "1":"0")
                    .where("uid")
                    .equals(playerUUID.toString())
                    .build();

            if (sqlQuery.getRowsAffected() <= 0) {
                player.sendMessage(Text.of(TextColors.RED, "[TE] Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
                logger.warn("[TE] An error occurred while updating the notification state in the database!");
            }
        } else {
            accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue(jobNotifications);

            try {
                loader.save(accountConfig);
            } catch (IOException e) {
                player.sendMessage(Text.of(TextColors.RED, "[TE] Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
                logger.warn("[TE] An error occurred while updating the notification state!");
            }
        }

        if (jobNotifications == true) {
            player.sendMessage(Text.of(TextColors.GRAY, "Notifications are now ", TextColors.GREEN, "ON"));
        } else {
            player.sendMessage(Text.of(TextColors.GRAY, "Notifications are now ", TextColors.RED, "OFF"));
        }
    }

    /**
     * Save the account configuration file
     *
     * @param forceSave Whether to force a save regardless of if conditions are met or not
     */
    public void saveAccountConfig(boolean forceSave) {
        confSaveRequested = true;

        if (totalEconomy.getSaveInterval() <= 0 || forceSave) {
            writeToDisk(forceSave);
        }
    }

    /**
     * Save the account configuration file
     */
    public void writeToDisk(boolean forceSave) {
        if (!forceSave && !confSaveRequested) {
            return;
        }

        try {
            loader.save(accountConfig);
            confSaveRequested = false;
        } catch (IOException e) {
            logger.error("[TE] An error occurred while saving the account configuration file!");
        }
    }

    /**
     * Get the account configuration file
     *
     * @return ConfigurationNode the account configuration
     */
    public ConfigurationNode getAccountConfig() {
        return accountConfig;
    }

    /**
     * Get the configuration manager
     *
     * @return ConfigurationLoader<CommentedConfigurationNode> the configuration loader for the account config
     */
    public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
        return loader;
    }

}
