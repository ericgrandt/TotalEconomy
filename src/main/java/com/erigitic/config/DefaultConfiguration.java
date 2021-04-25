package com.erigitic.config;

import com.erigitic.TotalEconomy;
import org.spongepowered.configurate.ConfigurationNode;

public class DefaultConfiguration extends Configuration {
    public DefaultConfiguration(TotalEconomy plugin) {
        super(plugin);
    }

    public String getConnectionString() {
        ConfigurationNode configurationNode = loadConfiguration("totaleconomy.conf");
        String connectionString = configurationNode.node("database", "connectionString").getString();
        String user = configurationNode.node("database", "connectionUser").getString();
        String password = configurationNode.node("database", "connectionPassword").getString();

        return String.format("%s?user=%s&password=%s", connectionString, user, password);
    }
}
