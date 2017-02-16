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
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class AccountManager implements EconomyService {
    private TotalEconomy totalEconomy;
    private Logger logger;
    private File accountsFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode accountConfig;

    private SQLHandler sqlHandler;

    private boolean databaseActive;

    public AccountManager(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();
        databaseActive = totalEconomy.isDatabaseActive();

        if (databaseActive) {
            sqlHandler = totalEconomy.getSqlHandler();

            setupDatabase();
        } else {
            setupConfig();
        }
    }

    /**
     * Setup the config file that will contain the user accounts.
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
            logger.warn("Could not create accounts config file!");
        }
    }

    public void setupDatabase() {
        sqlHandler.createDatabase();

        sqlHandler.createTable("accounts", "uid varchar(60) NOT NULL," +
                getDefaultCurrency().getName().toLowerCase() + "_balance decimal(19,2) NOT NULL DEFAULT '" + totalEconomy.getStartingBalance() + "'," +
                "job varchar(50) NOT NULL DEFAULT 'Unemployed'," +
                "job_notifications boolean NOT NULL DEFAULT TRUE," +
                "PRIMARY KEY (uid)");

        sqlHandler.createTable("levels", "uid varchar(60)," +
                "miner int(10) unsigned NOT NULL DEFAULT '1'," +
                "lumberjack int(10) unsigned NOT NULL DEFAULT '1'," +
                "warrior int(10) unsigned NOT NULL DEFAULT '1'," +
                "fisherman int(10) unsigned NOT NULL DEFAULT '1'," +
                "FOREIGN KEY (uid) REFERENCES totaleconomy.accounts(uid) ON DELETE CASCADE");

        sqlHandler.createTable("experience", "uid varchar(60)," +
                "miner int(10) unsigned NOT NULL DEFAULT '0'," +
                "lumberjack int(10) unsigned NOT NULL DEFAULT '0'," +
                "warrior int(10) unsigned NOT NULL DEFAULT '0'," +
                "fisherman int(10) unsigned NOT NULL DEFAULT '0'," +
                "FOREIGN KEY (uid) REFERENCES totaleconomy.accounts(uid) ON DELETE CASCADE");
    }

    /**
     * Reload the account config
     */
    public void reloadConfig() {
        try {
            accountConfig = loader.load();
            logger.info("Reloading account configuration file.");
        } catch (IOException e) {
            logger.warn("Could not reload account configuration file!");
        }
    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        String currencyName = getDefaultCurrency().getDisplayName().toPlain().toLowerCase();
        TEAccount playerAccount = new TEAccount(totalEconomy, this, uuid);

        try {
            if (!hasAccount(uuid)) {
                if (databaseActive) {
                    String[] colsArray = {};
                    String[] valsArray = {};

                    new SQLQuery.SQLQueryBuilder(sqlHandler.dataSource).insert("totaleconomy.accounts")
                            .columns("uid", currencyName + "_balance", "job", "job_notifications")
                            .values(uuid.toString(), playerAccount.getDefaultBalance(getDefaultCurrency()).toString(), "Unemployed", String.valueOf(totalEconomy.hasJobNotifications()))
                            .build();

//                    sqlHandler.insert("accounts", colsArray, valsArray);
                    sqlHandler.insert("levels", "uid", "'" + uuid.toString() + "'");
                    sqlHandler.insert("experience", "uid", "'" + uuid.toString() + "'");
                } else {
                    accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(playerAccount.getDefaultBalance(getDefaultCurrency()));
                    accountConfig.getNode(uuid.toString(), "job").setValue("unemployed");
                    accountConfig.getNode(uuid.toString(), "jobnotifications").setValue(totalEconomy.hasJobNotifications());
                    loader.save(accountConfig);
                }
            }
        } catch (IOException e) {
            logger.warn("Could not create account!");
        }

        return Optional.of(playerAccount);
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        String currencyName = getDefaultCurrency().getDisplayName().toPlain().toLowerCase();
        TEVirtualAccount virtualAccount = new TEVirtualAccount(totalEconomy, this, identifier);

        try {
            // TODO: Create new table for virtual accounts and store all virtual account data within. IF DATABASE ENABLED.
            if (accountConfig.getNode(identifier, currencyName + "-balance").getValue() == null) {
                accountConfig.getNode(identifier, currencyName + "-balance").setValue(virtualAccount.getDefaultBalance(getDefaultCurrency()));

                loader.save(accountConfig);
            }
        } catch (IOException e) {
            logger.warn("Could not create account!");
        }

        return Optional.of(virtualAccount);
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        if (databaseActive) {
            Optional<ResultSet> resultSetOpt = sqlHandler.select("uid", "accounts", "uid", uuid.toString());

            if (resultSetOpt.isPresent()) {
                ResultSet resultSet = resultSetOpt.get();
                boolean recordExists = sqlHandler.recordExists(resultSet);

                sqlHandler.close(resultSet);

                return recordExists;
            } else {
                return false;
            }
        } else {
            return accountConfig.getNode(uuid.toString()).getValue() != null;
        }
    }

    @Override
    public boolean hasAccount(String identifier) {
        return accountConfig.getNode(identifier).getValue() != null;
    }

    @Override
    public Currency getDefaultCurrency() {
        return totalEconomy.getDefaultCurrency();
    }

    @Override
    public Set<Currency> getCurrencies() {
        return new HashSet<>();
    }

    @Override
    public void registerContextCalculator(ContextCalculator calculator) {

    }

    /**
     * Toggle a player's exp/money notifications for jobs
     *
     * @param player an object representing the player toggling notifications
     */
    public void toggleNotifications(Player player) {
        boolean notify = true;
        UUID uuid = player.getUniqueId();

        if (databaseActive) {
            Optional<ResultSet> resultSetOp = sqlHandler.select("job_notifications", "accounts", "uid", uuid.toString());

            if (resultSetOp.isPresent()) {
                try {
                    ResultSet resultSet = resultSetOp.get();

                    if (resultSet.next()) {
                        notify = !resultSet.getBoolean(1);

                        int result = sqlHandler.update("accounts", "job_notifications", notify ? "1":"0", "uid", uuid.toString());

                        if (result == 0)
                            player.sendMessage(Text.of(TextColors.RED, "[SQL] Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
                    } else {
                        player.sendMessage(Text.of(TextColors.RED, "[SQL] Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
                    }

                    sqlHandler.close(resultSet);
                } catch (SQLException e) {
                    player.sendMessage(Text.of(TextColors.RED, "[SQL] Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
                    logger.warn("Error updating notification state in database!" + e.getMessage());
                }
            }
        } else {
            notify = !accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").getBoolean(true);

            accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue(notify);

            try {
                loader.save(accountConfig);
            } catch (IOException e) {
                player.sendMessage(Text.of(TextColors.RED, "Error toggling notifications! Try again. If this keeps showing up, notify the server owner or plugin developer."));
                logger.warn("Could not update notification state in configuration!");
            }
        }

        if (notify == true)
            player.sendMessage(Text.of(TextColors.GRAY, "Notifications are now ", TextColors.GREEN, "ON"));
        else
            player.sendMessage(Text.of(TextColors.GRAY, "Notifications are now ", TextColors.RED, "OFF"));
    }

    /**
     * Save the account configuration file
     */
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
