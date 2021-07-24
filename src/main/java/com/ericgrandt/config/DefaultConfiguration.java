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

@ConfigSerializable
class DatabaseSettings {
    @Setting
    private String connectionString = "jdbc:mysql://localhost:3306/totaleconomy";

    @Setting
    private String user = "user";

    @Setting
    private String password = "password";

    public String getConnectionString() {
        return  connectionString;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}