package com.ericgrandt.totaleconomy.config;

import com.ericgrandt.totaleconomy.model.Config;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;

public class ConfigLoader {
    public static Config from(FileConfiguration fileConfig) {
        return new Config(
            new Config.DatabaseConfig(
                fileConfig.getString("database.url"),
                fileConfig.getString("database.user"),
                fileConfig.getString("database.password")
            ),
            new Config.DefaultCurrencyConfig(
                fileConfig.getString("defaultCurrency.code", "USD"),
                fileConfig.getString("defaultCurrency.name", "Dollar"),
                fileConfig.getString("defaultCurrency.pluralName", "Dollars"),
                fileConfig.getString("defaultCurrency.symbol", "$"),
                fileConfig.getInt("defaultCurrency.fractionalDigits", 2),
                BigDecimal.valueOf(fileConfig.getDouble("defaultCurrency.startingBalance", 100))
            )
        );
    }
}
