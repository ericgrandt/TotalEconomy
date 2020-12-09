package com.erigitic.config;

import com.erigitic.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;

public class DefaultConfiguration extends Configuration {
    public DefaultConfiguration(TotalEconomy plugin) {
        super(plugin);
    }

    public String getConnectionString() {
        ConfigurationNode configurationNode = loadConfiguration("totaleconomy.conf");
        String connectionString = configurationNode.getNode("database", "connectionString").getString();
        String user = configurationNode.getNode("database", "connectionUser").getString();
        String password = configurationNode.getNode("database", "connectionPassword").getString();

        return String.format("%s?user=%s&password=%s", connectionString, user, password);
    }
}
