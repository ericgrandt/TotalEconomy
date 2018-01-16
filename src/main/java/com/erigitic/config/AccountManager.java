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

import com.erigitic.config.account.TEAccountBase;
import com.erigitic.config.account.TEConfigAccount;
import com.erigitic.config.account.TESqlAccount;
import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.SqlManager;
import com.erigitic.sql.SqlQuery;
import com.erigitic.util.MessageManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.user.UserStorageService;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountManager implements EconomyService {
    private TotalEconomy totalEconomy;
    private MessageManager messageManager;
    private Logger logger;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode accountConfig;

    private SqlManager sqlManager;

    private boolean databaseActive;

    private boolean confSaveRequested = false;

    /**
     * Constructor for the AccountManager class. Handles the initialization of necessary variables, setup of the database
     * or configuration files depending on main configuration value, and starts save script if setup.
     *
     * @param totalEconomy Main plugin class
     */
    public AccountManager(TotalEconomy totalEconomy, MessageManager messageManager, Logger logger) {
        this.totalEconomy = totalEconomy;
        this.messageManager = messageManager;
        this.logger = logger;

        databaseActive = totalEconomy.isDatabaseEnabled();

        if (databaseActive) {
            sqlManager = totalEconomy.getSqlManager();
        } else {
            setupConfig();

            if (totalEconomy.getSaveInterval() > 0) {
                setupAutosave();
            }
        }
    }

    /**
     * Setup the config file that will contain the user accounts
     */
    private void setupConfig() {
        File accountsFile = new File(totalEconomy.getConfigDir(), "accounts.conf");
        loader = HoconConfigurationLoader.builder().setFile(accountsFile).build();

        try {
            accountConfig = loader.load();

            if (!accountsFile.exists()) {
                loader.save(accountConfig);
            }

            // Automatic convert from the old storage format
            // === This can be removed somewhat upward the next updates ===
            final Pattern UUID_PATTERN = Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89aAbB][a-fA-F0-9]{3}-[a-fA-F0-9]{12})");
            final UserStorageService userStore = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            final AtomicInteger accountConvertCount = new AtomicInteger(0);
            final AtomicInteger balanceConvertCount = new AtomicInteger(0);
            Set<? extends Map.Entry<Object, ? extends ConfigurationNode>> nodes = accountConfig.getChildrenMap().entrySet();

            nodes.parallelStream()
                 .forEach(e -> {
                     Object accountKey = e.getKey();
                     ConfigurationNode accountNode = e.getValue();

                     if (!(accountKey instanceof String)) {
                         return;
                     }

                     if (!accountNode.getNode("balance").isVirtual()) {
                         return;
                     }
                     Set<? extends Map.Entry<Object, ? extends ConfigurationNode>> allSubNodes = accountNode.getChildrenMap().entrySet();

                     for (Map.Entry<Object, ? extends ConfigurationNode> subNodeEntry : allSubNodes) {
                         Object subKeyObject = subNodeEntry.getKey();

                         if (!(subKeyObject instanceof String)) {
                             return;
                         }

                         if (!((String) subKeyObject).endsWith("-balance")) {
                             String balance = ((String) subKeyObject).replaceAll("-balance", "");
                             accountNode.getNode("balance", balance).setValue(subNodeEntry.getValue().getValue());
                             balanceConvertCount.incrementAndGet();
                         }
                         accountNode.removeChild(subKeyObject);
                     }
                     Matcher matcher = UUID_PATTERN.matcher(((String) accountKey));
                     String sUUID;
                     String displayName;

                     // When a UUID has been found, use it
                     // When the key contained other information use that as the display name
                     // Otherwise we'll need to create a UUID
                     if (matcher.find()) {
                         sUUID = matcher.group(1).toLowerCase();
                         displayName = UUID_PATTERN.matcher(((String) accountKey)).replaceAll("");
                     } else {
                         sUUID = UUID.randomUUID().toString().toLowerCase();
                         displayName = ((String) accountKey);
                     }

                     // Convert to the new format if the key hasn't been only the UUID before
                     if (!displayName.isEmpty()) {
                         ConfigurationNode newFormatNode = accountConfig.getNode(sUUID);
                         newFormatNode.mergeValuesFrom(accountNode);
                         newFormatNode.getNode("displayname").setValue(displayName);
                         accountConfig.removeChild(accountKey);
                         accountConvertCount.incrementAndGet();
                     }
                 });

            if (balanceConvertCount.get() > 0) {
                logger.warn(balanceConvertCount.get() + " balances were converted from the old format.");
            }

            if (accountConvertCount.get() > 0) {
                logger.warn(accountConvertCount.get() + " accounts have been converted from the old format.");
            }
            // End of automatic converter code

        } catch (IOException e) {
            logger.warn("Error creating accounts configuration file!");
        }
    }

    /**
     * Setup a scheduler that handles the saving of the account configuration file
     */
    private void setupAutosave() {
        Sponge.getScheduler().createTaskBuilder().interval(totalEconomy.getSaveInterval(), TimeUnit.SECONDS)
                .execute(() -> {
                    if (confSaveRequested) {
                        saveConfiguration();
                        confSaveRequested = false;
                    }
                }).submit(totalEconomy);
    }

    /**
     * Reload the account config
     */
    public void reloadConfig() {
        try {
            accountConfig = loader.load();
            logger.info("Reloading account configuration file.");
        } catch (IOException e) {
            logger.warn("An error occurred while reloading the account configuration file!");
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

        TEAccountBase account;
        if (databaseActive) {
            account = new TESqlAccount(totalEconomy, logger, sqlManager.getDataSource(), uuid);
        } else {
            account = new TEConfigAccount(totalEconomy, accountConfig.getNode(uuid.toString()), uuid);
        }

        boolean hasAccount = hasAccount(uuid);
        try {
            // If the account does not exist create it by setting the default value of the default currency.
            // That should create the account for both db and flat file storage.
            if (!hasAccount) {
                account.setBalance(getDefaultCurrency(), ((TECurrency) getDefaultCurrency()).getStartingBalance(), Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

            } else if (!databaseActive) {
                addNewCurrenciesToAccount(account);
            }
        } catch (Exception e) {
            logger.warn("An error occurred while creating a new account!");
        }

        return Optional.of(account);
    }
    /**
     * Gets or creates a virtual account for the passed in identifier
     *
     * WARNING: SQL-Injection! As this is likely to be user input SANITIZE THE INPUT PARAM!
     *
     * @param identifier The virtual accounts identifier
     * @return Optional<Account> The virtual account that was retrieved or created
     */
    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {

        Optional<UUID> accountUUIDOpt = getVirtualAccountUUID(identifier);
        // When no UUID has been found create a new one
        UUID accountUUID = accountUUIDOpt.orElse(UUID.randomUUID());

        TEAccountBase account;
        if (databaseActive) {
            account = new TESqlAccount(totalEconomy, logger, sqlManager.getDataSource(), accountUUID);
        } else {
            account = new TEConfigAccount(totalEconomy, accountConfig.getNode(accountUUID.toString()), accountUUID);
        }

        // If the account does not exist create it
        if (databaseActive && !hasAccount(accountUUID)) {
            String queryString = "INSERT INTO accounts (`uid`, `displayname`) VALUES (:account_uid, :displayname)";

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("account_uid", accountUUID.toString());
                query.setParameter("displayname", account.isVirtual() ? null : identifier);

                if (query.getStatement().executeUpdate() != 1) {
                    throw new SQLException("Unexpected row count!");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create account: " + accountUUID.toString(), e);
            }
        }

        try {
            // If the account did not exist create it by setting the default value of the default currency.
            // That should create the account for the flat file storage. (DB handled above)
            if (!accountUUIDOpt.isPresent()) {
                account.setBalance(getDefaultCurrency(), ((TECurrency) getDefaultCurrency()).getStartingBalance(), Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

            } else if (!databaseActive) {
                addNewCurrenciesToAccount(account);
            }
        } catch (IOException e) {
            logger.warn("An error occurred while creating a new virtual account!");
        }
        return Optional.of(account);
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
            String queryString = "SELECT uid FROM accounts WHERE uid = :account_uid";

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("account_uid", uuid.toString());
                PreparedStatement statement = query.getStatement();
                statement.executeQuery();
                return statement.getResultSet().next();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to check account existance for " + uuid.toString(), e);
            }
        } else {
            return accountConfig.getNode(uuid.toString()).isVirtual();
        }
    }

    /**
     * Determines if a virtual account is associated with the passed in UUID
     * It is better to use the Optional from {@link #getVirtualAccountUUID(String)} for this as this also returns the value for further use
     *
     * WARNING: SQL-Injection! As this is likely to be user input SANITIZE THE INPUT PARAM!
     *
     * @param identifier The identifier to check for an account
     * @return boolean Whether or not a virtual account is associated with the passed in identifier
     */
    @Override
    public boolean hasAccount(String identifier) {
        return getVirtualAccountUUID(identifier).isPresent();
    }

    /**
     * Returns the UUID of an account found by the identifier.
     * Both the `uid` and the `displayname` columns are searched.
     *
     * @param identifier The search string
     */
    public Optional<UUID> getVirtualAccountUUID(String identifier) {
        UUID resultUUID = null;

        if (databaseActive) {
            String queryString = "SELECT `uid`,`displayname` FROM `accounts` WHERE `uid` = :search OR `displayname` = :search";

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("search", identifier);
                PreparedStatement statement = query.getStatement();
                statement.executeQuery();
                ResultSet result = statement.getResultSet();
                resultUUID = UUID.fromString(result.getString("uid"));

                // Do we have more than one result? Identifier was not unique thus not found.
                if (result.next()) {
                    resultUUID = null;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to search account by: " + identifier, e);
            }

        } else {

            // If that node exists we've been supplied with an existing UUID.
            // Otherwise we'll search for it.
            if (accountConfig.getNode(identifier).hasMapChildren()) {
                resultUUID = UUID.fromString(identifier);

            } else {

                for (Map.Entry<Object, ? extends ConfigurationNode> entry : accountConfig.getChildrenMap().entrySet()) {
                    if (!(entry.getKey() instanceof String)) {
                        continue;
                    }
                    ConfigurationNode displayNameNode = entry.getValue().getNode("displayname");

                    if (!displayNameNode.isVirtual() && identifier.equals(displayNameNode.getString(null))) {
                        resultUUID = UUID.fromString(((String) entry.getKey()));
                        break;
                    }
                }
            }
        }
        return Optional.ofNullable(resultUUID);
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
     * Checks if a unique account has a balance for each currency. If one doesn't exist, a new balance for that currency will be
     * added and set to that currencies starting balance.
     *
     * @param playerAccount The unique account to add the balance to
     * @throws IOException
     */
    private void addNewCurrenciesToAccount(TEAccountBase playerAccount) throws IOException {
        for (Currency currency : totalEconomy.getCurrencies()) {
            TECurrency teCurrency = (TECurrency) currency;

            if (!playerAccount.hasBalance(teCurrency)) {
                playerAccount.setBalance(teCurrency, playerAccount.getDefaultBalance(teCurrency), Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
            }
        }

        requestConfigurationSave();
    }

    /**
     * Gets the passed in player's notification state
     *
     * @param player The {@link Player} who's notification state to get
     * @return boolean The notification state
     */
    public boolean getJobNotificationState(Player player) {
        if (databaseActive) {

            String queryString = "SELECT value FROM accounts_options WHERE uid = :account_uid AND ident = 'job_notifications'";

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("account_uid", player.getUniqueId().toString());
                PreparedStatement statement = query.getStatement();
                statement.executeQuery();

                ResultSet result = statement.getResultSet();
                if (!result.next()) {
                    return totalEconomy.isJobNotificationEnabled();
                }
                boolean res = "TRUE".equals(result.getString("value"));

                if (result.next()) {
                    throw new SQLException("Too many results!");
                }
                return res;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get notifications state for " + player.getUniqueId().toString() + "/" + player.getName(), e);
            }
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

        if (databaseActive) {
            String queryString = "INSERT INTO accounts_options (`uid`, `ident`, `value`) VALUES (:account_uid, 'job_notifications', :job_notifs) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)";

            try (SqlQuery query = new SqlQuery(totalEconomy.getSqlManager().getDataSource(), queryString)) {
                query.setParameter("account_uid", player.getUniqueId().toString());
                query.setParameter("job_notifs", jobNotifications ? "TRUE" : "FALSE" );

                if (query.getStatement().executeUpdate() != 2) {
                    throw new SQLException("Unexpected update count!");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to toggle job notifications for " + player.getUniqueId().toString() + "/" + player.getName(), e);
            }
        } else {
            accountConfig.getNode(player.getUniqueId().toString(), "jobnotifications").setValue(jobNotifications);
            totalEconomy.requestAccountConfigurationSave();
        }

        if (jobNotifications) {
            player.sendMessage(messageManager.getMessage("notifications.on"));
        } else {
            player.sendMessage(messageManager.getMessage("notifications.off"));
        }
    }

    /**
     * Request for the account configuration file to be saved
     */
    public void requestConfigurationSave() {
        if (totalEconomy.getSaveInterval() > 0) {
            confSaveRequested = true;
        } else {
            saveConfiguration();
        }
    }

    /**
     * Save the account configuration file
     */
    private void saveConfiguration() {
        try {
            loader.save(accountConfig);
        } catch (IOException e) {
            logger.error("An error occurred while saving the account configuration file!");
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
