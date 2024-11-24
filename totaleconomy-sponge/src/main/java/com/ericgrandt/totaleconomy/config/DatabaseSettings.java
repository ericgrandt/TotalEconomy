package com.ericgrandt.totaleconomy.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class DatabaseSettings {
    private String url = "jdbc:mysql://localhost:3306/totaleconomy";
    private String user = "root";
    private String password = "password";

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
