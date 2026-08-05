package com.ericgrandt.totaleconomy.jobs.paper.config;

import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigLoaderTest {
    @Test
    public void from_ShouldParseConfig() throws InvalidConfigurationException {
        // Arrange
        var config = buildTestConfig();

        // Act
        var entries = List.of(new Config.Job.Action.Entry("COAL_ORE", 10, BigDecimal.valueOf(3.0)));

        var actual = ConfigLoader.from(config);
        var expected = new Config(
            List.of(
                new Config.Job(
                    "miner",
                    "Miner",
                    "Mine ores",
                    List.of(new Config.Job.Action(JobEnums.ActionType.BLOCK_BREAK, entries))
                )
            ),
            new Config.Settings(
                100,
                new Config.Settings.XPCurve(
                    JobEnums.XPCurveType.QUADRATIC,
                    100
                ),
                new Config.Settings.PayoutMultiplier(
                    JobEnums.PayoutMultiplierType.LINEAR,
                    1,
                    0.01
                )
            )
        );

        // Assert
        assertEquals(expected, actual);
    }

    private @NonNull YamlConfiguration buildTestConfig() throws InvalidConfigurationException {
        var config = new YamlConfiguration();
        config.loadFromString("""
            jobs:
              miner:
                id: miner
                displayName: "Miner"
                description: "Mine ores"
                actions:
                  BLOCK_BREAK:
                    entries:
                      COAL_ORE:
                        xp: 10
                        payout: 3.0
            
            settings:
              maxLevel: 100
              xpCurve:
                type: QUADRATIC
                baseXP: 100
              payoutMultiplier:
                type: LINEAR
                base: 1.0
                perLevel: 0.01
            """);
        return config;
    }
}