package com.erigitic;

import com.erigitic.commands.CommandRegister;
import com.erigitic.config.DefaultConfiguration;
import com.erigitic.data.Database;
import com.erigitic.economy.TEEconomyService;
import com.erigitic.player.PlayerListener;
import com.erigitic.shop.data.ImmutableShopData;
import com.erigitic.shop.data.MutableShopData;
import com.erigitic.shop.data.ShopDataBuilder;
import com.erigitic.shop.data.ShopKeys;
import com.google.inject.Inject;
import java.io.File;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;

@Plugin(id = "totaleconomy", name = "Total Economy", version = "2.0.0", description = "All in one economy plugin for Minecraft and Sponge")
public class TotalEconomy {
    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File config;

    @Inject
    private PluginContainer pluginContainer;

    private EventContext eventContext;

    private static TotalEconomy plugin;
    private DefaultConfiguration defaultConfiguration;
    private Database database;
    private TEEconomyService economyService;

    public TotalEconomy() {
        plugin = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, pluginContainer).build();

        defaultConfiguration = new DefaultConfiguration();
        defaultConfiguration.loadConfiguration();

        database = new Database();
        database.setup();

        economyService = new TEEconomyService();
        Sponge.getServiceManager().setProvider(this, EconomyService.class, economyService);

        // TODO: Move to separate class
        // Have to initialize keys otherwise they won't load in time
        ShopKeys.initShopKeys();
        DataRegistration.builder()
            .id("shop")
            .dataClass(MutableShopData.class)
            .immutableClass(ImmutableShopData.class)
            .builder(new ShopDataBuilder())
            .build();
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        CommandRegister commandRegister = new CommandRegister();
        commandRegister.registerCommands();

        Sponge.getEventManager().registerListeners(this, new PlayerListener());
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("TotalEconomy started successfully");
    }

    public EventContext getEventContext() {
        return eventContext;
    }

    public static TotalEconomy getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getConfigDir() {
        return configDir;
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public DefaultConfiguration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public Database getDatabase() {
        return database;
    }

    public TEEconomyService getEconomyService() {
        return economyService;
    }
}
