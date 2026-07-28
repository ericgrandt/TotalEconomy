package com.ericgrandt.totaleconomy.jobs.paper.config;

import com.ericgrandt.totaleconomy.jobs.config.TEJobConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLoader {
    public static TEJobConfig from(FileConfiguration fileConfig) {
        return new TEJobConfig(
        );
    }
}
