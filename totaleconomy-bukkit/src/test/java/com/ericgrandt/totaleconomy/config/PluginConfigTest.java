package com.ericgrandt.totaleconomy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PluginConfigTest {
    private static final FileConfiguration configuration = new YamlConfiguration();

    @BeforeAll
    public static void setup() {
        configuration.addDefault("database.url", "testUrl");
        configuration.addDefault("database.user", "testUser");
        configuration.addDefault("database.password", "testPassword");

        configuration.addDefault("features.jobs", true);
    }

    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnDatabaseUrl() {
        // Arrange
        PluginConfig sut = new PluginConfig(configuration);

        // Act
        String actual = sut.getDatabaseUrl();
        String expected = "testUrl";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnDatabaseUser() {
        // Arrange
        PluginConfig sut = new PluginConfig(configuration);

        // Act
        String actual = sut.getDatabaseUser();
        String expected = "testUser";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnDatabasePassword() {
        // Arrange
        PluginConfig sut = new PluginConfig(configuration);

        // Act
        String actual = sut.getDatabasePassword();
        String expected = "testPassword";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnMapOfFeatureStatuses() {
        // Arrange
        PluginConfig sut = new PluginConfig(configuration);

        // Act
        Map<String, Boolean> actual = sut.getFeatures();
        Map<String, Boolean> expected = Map.of("jobs", true);

        // Assert
        assertEquals(expected, actual);
    }
}
