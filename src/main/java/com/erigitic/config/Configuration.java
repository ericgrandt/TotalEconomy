package com.erigitic.config;

import com.erigitic.TotalEconomy;
import java.io.File;
import java.io.IOException;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public abstract class Configuration {
    private final TotalEconomy plugin;

    public Configuration(TotalEconomy plugin) {
        this.plugin = plugin;
    }

    protected ConfigurationNode loadConfiguration(String configName) {
        File config = new File(plugin.getConfigDir(), configName);
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().file(config).build();

        try {
            return loader.load();
        } catch (IOException e) {
            plugin.getLogger().warn(String.format("%s file could not be loaded", configName));
        }

        return null;
    }
}
