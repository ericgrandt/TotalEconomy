package com.ericgrandt.totaleconomy.config;

import com.ericgrandt.totaleconomy.common.config.Config;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig implements Config {
    private final FileConfiguration config;

    public PluginConfig(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public String getDatabaseUrl() {
        return config.getString("database.url");
    }

    @Override
    public String getDatabaseUser() {
        return config.getString("database.user");
    }

    @Override
    public String getDatabasePassword() {
        return config.getString("database.password");
    }

    @Override
    public Map<String, Boolean> getFeatures() {
        Map<String, Boolean> features = new HashMap<>();
        features.put("jobs", config.getBoolean("features.jobs"));
        return features;
    }
}
