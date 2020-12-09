package com.erigitic.config;

import com.erigitic.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public abstract class Configuration {
    private final TotalEconomy plugin;

    public Configuration(TotalEconomy plugin) {
        this.plugin = plugin;
    }

    protected ConfigurationNode loadConfiguration(String configName) {
        File config = new File(plugin.getConfigDir(), configName);
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setFile(config).build();

        try {
            return loader.load();
        } catch (IOException e) {
            plugin.getLogger().warn(String.format("%s file could not be loaded", configName));
        }

        return null;
    }
}
