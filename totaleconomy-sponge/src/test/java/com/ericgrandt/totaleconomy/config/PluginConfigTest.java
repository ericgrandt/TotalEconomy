package com.ericgrandt.totaleconomy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PluginConfigTest {
    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnDatabaseUrl() {
        // Arrange
        PluginConfig sut = new PluginConfig();

        // Act
        String actual = sut.getDatabaseUrl();
        String expected = "jdbc:mysql://localhost:3306/totaleconomy";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnDatabaseUser() {
        // Arrange
        PluginConfig sut = new PluginConfig();

        // Act
        String actual = sut.getDatabaseUser();
        String expected = "root";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnDatabasePassword() {
        // Arrange
        PluginConfig sut = new PluginConfig();

        // Act
        String actual = sut.getDatabasePassword();
        String expected = "password";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getDatabaseUrl_ShouldReturnMapOfFeatureStatuses() {
        // Arrange
        PluginConfig sut = new PluginConfig();

        // Act
        Map<String, Boolean> actual = sut.getFeatures();
        Map<String, Boolean> expected = new HashMap<>();
        expected.put("jobs", true);

        // Assert
        assertEquals(expected, actual);
    }
}
