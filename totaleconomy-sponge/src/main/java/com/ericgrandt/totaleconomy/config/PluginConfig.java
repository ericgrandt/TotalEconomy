package com.ericgrandt.totaleconomy.config;

import com.ericgrandt.totaleconomy.common.config.Config;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PluginConfig implements Config {
    @Setting("database")
    private final DatabaseSettings databaseSettings = new DatabaseSettings();

    @Setting("features")
    private final FeatureSettings featureSettings = new FeatureSettings();

    @Override
    public String getDatabaseUrl() {
        return databaseSettings.getUrl();
    }

    @Override
    public String getDatabaseUser() {
        return databaseSettings.getUser();
    }

    @Override
    public String getDatabasePassword() {
        return databaseSettings.getPassword();
    }

    @Override
    public Map<String, Boolean> getFeatures() {
        return featureSettings.getFeatures();
    }
}
