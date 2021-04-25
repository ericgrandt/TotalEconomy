package com.erigitic.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class DefaultConfiguration {
    @Setting("database")
    private DatabaseSettings databaseSettings = new DatabaseSettings();

    public String getConnectionString() {
        return String.format(
            "%s?user=%s&password=%s",
            databaseSettings.getConnectionString(),
            databaseSettings.getUser(),
            databaseSettings.getPassword()
        );
    }
}

@ConfigSerializable
class DatabaseSettings {
    private String connectionString = "jdbc:mysql://localhost:3306/totaleconomy";
    private String user = "user";
    private String password = "password";

    public String getConnectionString() {
        return connectionString;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}