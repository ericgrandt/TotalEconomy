package com.ericgrandt.totaleconomy.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class DatabaseSettings {
    @Setting
    private final String url = "jdbc:mysql://localhost:3306/totaleconomy";

    @Setting
    private final String user = "root";

    @Setting
    private final String password = "password";

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
