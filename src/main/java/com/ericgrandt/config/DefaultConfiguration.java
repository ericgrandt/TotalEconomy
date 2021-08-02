package com.ericgrandt.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class DefaultConfiguration {
    @Setting("database")
    private final DatabaseSettings databaseSettings = new DatabaseSettings();

    public String getConnectionString() {
        return String.format(
            "%s?user=%s&password=%s",
            databaseSettings.getConnectionString(),
            databaseSettings.getUser(),
            databaseSettings.getPassword()
        );
    }
}