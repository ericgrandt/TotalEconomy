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

package com.erigitic.main;

import com.erigitic.commands.*;
import com.erigitic.config.AccountManager;
import com.erigitic.config.TECurrency;
import com.erigitic.config.TECurrencyRegistryModule;
import com.erigitic.jobs.TEJobManager;
import com.erigitic.sql.SQLHandler;
import com.erigitic.util.MessageHandler;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Plugin(id = "totaleconomy", name = "Total Economy", version = "1.7.0-dev.4", description = "All in one economy plugin for Minecraft/Sponge")
public class TotalEconomy {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConf;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    private Game game;

    @Inject
    private PluginContainer pluginContainer;

    private UserStorageService userStorageService;

    private ConfigurationNode config;

    private TECurrency defaultCurrency;

    private AccountManager accountManager;
    private TEJobManager teJobManager;
    private MessageHandler messageHandler;

    private TECurrencyRegistryModule teCurrencyRegistryModule;

    private HashSet<Currency> currencies = new HashSet<>();

    private boolean loadJobs = true;
    private boolean jobPermissions = false;
    private boolean jobNotifications = true;
    private boolean loadSalary = true;
    private boolean loadMoneyCap = false;
    private boolean databaseActive = false;

    private String languageTag;

    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    private BigDecimal moneyCap;

    private int saveInterval;

    private SQLHandler sqlHandler;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        setupConfig();

        // Loop through each currency and store them in a Collection<Currency>. Sets the default currency as well.
        config.getNode("currency").getChildrenMap().keySet().forEach(currencyName -> {
            ConfigurationNode currencyNode = config.getNode("currency", currencyName.toString());

            String currencySingular = currencyNode.getNode("currency-singular").getString();
            String currencyPlural = currencyNode.getNode("currency-plural").getString();
            String currencySymbol = currencyNode.getNode("symbol").getString();
            boolean isDefault = currencyNode.getNode("default").getBoolean();
            boolean prefixSymbol = currencyNode.getNode("prefix-symbol").getBoolean();
            boolean isTransferable = currencyNode.getNode("transferable").getBoolean();
            BigDecimal startBalance = new BigDecimal(currencyNode.getNode("startbalance").getDouble());

            TECurrency currency = new TECurrency(
                    Text.of(currencySingular),
                    Text.of(currencyPlural),
                    Text.of(currencySymbol),
                    2,
                    isDefault,
                    prefixSymbol,
                    isTransferable,
                    startBalance
            );

            if (isDefault) {
                defaultCurrency = currency;
            }

            currencies.add(currency);
        });

        loadJobs = config.getNode("features", "jobs", "enable").getBoolean();
        loadSalary = config.getNode("features", "jobs", "salary").getBoolean();
        databaseActive = config.getNode("database", "enable").getBoolean();
        jobPermissions = config.getNode("features", "jobs", "permissions").getBoolean();
        jobNotifications = config.getNode("features", "jobs", "notifications").getBoolean();
        loadMoneyCap = config.getNode("features", "moneycap", "enable").getBoolean();

        languageTag = config.getNode("language").getString("en");

        if (databaseActive) {
            databaseUrl = config.getNode("database", "url").getString();
            databaseUser = config.getNode("database", "user").getString();
            databasePassword = config.getNode("database", "password").getString();

            sqlHandler = new SQLHandler(this);
        }

        saveInterval = config.getNode("save-interval").getInt(30);

        messageHandler = new MessageHandler(this, Locale.forLanguageTag(languageTag));
        accountManager = new AccountManager(this);
        teCurrencyRegistryModule = new TECurrencyRegistryModule(this);

        game.getServiceManager().setProvider(this, EconomyService.class, accountManager);

        //Only setup job stuff if config is set to load jobs
        if (loadJobs) {
            teJobManager = new TEJobManager(this);
        }

        if (loadMoneyCap) {
            moneyCap = BigDecimal.valueOf(config.getNode("features", "moneycap", "amount").getFloat()).setScale(2, BigDecimal.ROUND_DOWN);
        }

        // Allows for retrieving of all/individual currencies in Total Economy by other plugins
        game.getRegistry().registerModule(Currency.class, teCurrencyRegistryModule);
    }

    @Listener
    public void init(GameInitializationEvent event) {
        createAndRegisterCommands();

        if (loadJobs) {
            game.getEventManager().registerListeners(this, teJobManager);
        }
    }

    @Listener
    public void postInit(GamePostInitializationEvent event) {

    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        userStorageService = game.getServiceManager().provideUnchecked(UserStorageService.class);

        logger.info("Total Economy Started");
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        logger.info("Total Economy Stopping");

        if (!databaseActive) {
            accountManager.saveAccountConfig(true);
        }
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        logger.info("Total Economy Stopped");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

        accountManager.getOrCreateAccount(player.getUniqueId());
    }

    /**
     * Reloads configuration files
     *
     * @param event
     */
    @Listener
    public void onGameReload(GameReloadEvent event) {
        if (loadJobs) {
            teJobManager.reloadJobsAndSets();
        }

        accountManager.reloadConfig();
    }

    /**
     * Setup the default config file, totaleconomy.conf.
     */
    private void setupConfig() {
        try {
            config = loader.load();

            if (!defaultConf.exists()) {
                config.getNode("database", "enable").setValue(databaseActive);
                config.getNode("database", "url").setValue("mysql://[IP]:[PORT]/[DATABASE]");
                config.getNode("database", "user").setValue("");
                config.getNode("database", "password").setValue("");

                config.getNode("features", "jobs", "enable").setValue(loadJobs);
                config.getNode("features", "jobs", "salary").setValue(loadSalary);
                config.getNode("features", "jobs", "permissions").setValue(jobPermissions);
                config.getNode("features", "jobs", "notifications").setValue(true);
                config.getNode("features", "moneycap", "enable").setValue(loadMoneyCap);
                config.getNode("features", "moneycap", "amount").setValue(10000000);

                config.getNode("currency", "dollar", "currency-singular").setValue("Dollar");
                config.getNode("currency", "dollar", "currency-plural").setValue("Dollars");
                config.getNode("currency", "dollar", "symbol").setValue("$");
                config.getNode("currency", "dollar", "default").setValue(true);
                config.getNode("currency", "dollar", "prefix-symbol").setValue(true);
                config.getNode("currency", "dollar", "transferable").setValue(true);
                config.getNode("currency", "dollar", "startbalance").setValue("100");

                config.getNode("language").setValue("en");
                config.getNode("save-interval").setValue(30);

                loader.save(config);
            }
        } catch (IOException e) {
            logger.warn("[TE] Main configuration file could not be loaded/created/changed!");
        }
    }

    private void createAndRegisterCommands() {
        CommandSpec adminPayCommand = CommandSpec.builder()
                .description(Text.of("Pay a player without removing money from your balance."))
                .permission("totaleconomy.command.adminpay")
                .executor(new AdminPayCommand(this))
                .arguments(GenericArguments.user(Text.of("player")),
                        GenericArguments.string(Text.of("amount")),
                        GenericArguments.optional(GenericArguments.string(Text.of("currencyName"))))
                .build();

        CommandSpec balanceCommand = CommandSpec.builder()
                .description(Text.of("Display your balance"))
                .permission("totaleconomy.command.balance")
                .executor(new BalanceCommand(this))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("currencyName"))))
                .build();

        CommandSpec balanceTopCommand = CommandSpec.builder()
                .description(Text.of("Display top balances"))
                .permission("totaleconomy.command.balancetop")
                .executor(new BalanceTopCommand(this))
                .build();

        CommandSpec payCommand = CommandSpec.builder()
                .description(Text.of("Pay another player"))
                .permission("totaleconomy.command.pay")
                .executor(new PayCommand(this))
                .arguments(GenericArguments.player(Text.of("player")),
                        GenericArguments.string(Text.of("amount")),
                        GenericArguments.optional(GenericArguments.string(Text.of("currencyName"))))
                .build();

        CommandSpec setBalanceCommand = CommandSpec.builder()
                .description(Text.of("Set a player's balance"))
                .permission("totaleconomy.command.setbalance")
                .executor(new SetBalanceCommand(this))
                .arguments(GenericArguments.user(Text.of("player")),
                        GenericArguments.string(Text.of("amount")),
                        GenericArguments.optional(GenericArguments.string(Text.of("currencyName"))))
                .build();

        CommandSpec viewBalanceCommand = CommandSpec.builder()
                .description(Text.of("View the balance of another player"))
                .permission("totaleconomy.command.viewbalance")
                .executor(new ViewBalanceCommand(this))
                .arguments(GenericArguments.user(Text.of("player")),
                        GenericArguments.optional(GenericArguments.string(Text.of("currencyName"))))
                .build();

        //Only enables job commands if the value for jobs in config is set to true
        if (loadJobs) {
            game.getCommandManager().register(this, JobCommand.commandSpec(this), "job");
        }

        game.getCommandManager().register(this, payCommand, "pay");
        game.getCommandManager().register(this, adminPayCommand, "adminpay");
        game.getCommandManager().register(this, balanceCommand, "balance", "bal", "money");
        game.getCommandManager().register(this, viewBalanceCommand, "viewbalance", "vbal");
        game.getCommandManager().register(this, setBalanceCommand, "setbalance", "setbal");
        game.getCommandManager().register(this, balanceTopCommand, "balancetop", "baltop");
    }

    public HashSet<Currency> getCurrencies() {
        return currencies;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public TEJobManager getTEJobManager() {
        return teJobManager;
    }

    public MessageHandler getMessageHandler() { return messageHandler; }

    public TECurrencyRegistryModule getTECurrencyRegistryModule() { return teCurrencyRegistryModule; }

    public Logger getLogger() {
        return logger;
    }

    public File getConfigDir() {
        return configDir;
    }

    public Server getServer() {
        return game.getServer();
    }

    public Game getGame() { return game; }

    public PluginContainer getPluginContainer() { return pluginContainer; }

    public TECurrency getDefaultCurrency() {
        return defaultCurrency;
    }

    public boolean isLoadSalary() {
        return loadSalary;
    }

    public boolean isDatabaseActive() { return databaseActive; }

    public int getSaveInterval() {
        return saveInterval;
    }

    public boolean hasJobNotifications() { return jobNotifications; }

    public BigDecimal getMoneyCap() { return loadMoneyCap ? moneyCap : new BigDecimal(Double.MAX_VALUE); }

    public UserStorageService getUserStorageService() {
        return userStorageService;
    }

    public String getDatabaseUrl() { return databaseUrl; }

    public String getDatabaseUser() { return databaseUser; }

    public String getDatabasePassword() { return databasePassword; }

    public SQLHandler getSqlHandler() { return sqlHandler; }

}