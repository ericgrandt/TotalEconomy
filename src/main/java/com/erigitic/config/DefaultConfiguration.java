package com.erigitic.config;

import com.erigitic.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;

public class DefaultConfiguration {
    private final TotalEconomy plugin;
    private final PluginContainer pluginContainer;
    private final Logger logger;

    private final File config;
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode configurationNode;

    public DefaultConfiguration() {
        plugin = TotalEconomy.getPlugin();
        pluginContainer = plugin.getPluginContainer();
        logger = plugin.getLogger();

        config = new File(plugin.getConfigDir(), "totaleconomy.conf");
        loader = HoconConfigurationLoader.builder().setFile(config).build();
    }

    public void loadConfiguration() {
        try {
            if (!config.exists()) {
                pluginContainer.getAsset("totaleconomy.conf").get().copyToFile(config.toPath());
            }

            configurationNode = loader.load();
        } catch (IOException e) {
            logger.warn("Main configuration file could not be loaded");
        }
    }

    public String getConnectionString() {
        String connectionString = configurationNode.getNode("database", "connectionString").getString();
        String user = configurationNode.getNode("database", "connectionUser").getString();
        String password = configurationNode.getNode("database", "connectionPassword").getString();
        connectionString += String.format("?user=%s&password=%s", user, password);

        return connectionString;
    }
}
