package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.ericgrandt.totaleconomy.models.JobExperience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class JobExperienceBarTest {
    @Test
    @Tag("Unit")
    public void setExperienceBarName_ShouldChangeNameOfBossBar() {
        // Arrange
        JobExperience jobExperience = new JobExperience(
            "Miner",
            50,
            0,
            100,
            1
        );

        Player player = mock(Player.class);
        JobExperienceBar sut = new JobExperienceBar(player, null);

        // Act
        sut.setExperienceBarName(jobExperience, 25);

        Component actual = sut.getBossBar().name();
        Component expected = Component.text("Miner [+25 EXP]");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setProgress_WithBaseLevelExperience_ShouldSetProgressToZero() {
        // Arrange
        JobExperience jobExperience = new JobExperience(
            "Miner",
            50,
            50,
            100,
            2
        );

        Player player = mock(Player.class);
        JobExperienceBar sut = new JobExperienceBar(player, null);

        // Act
        sut.setProgress(jobExperience);

        float actual = sut.getBossBar().progress();
        float expected = 0.0f;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setProgress_WithHalfwayToNextLevel_ShouldSetProgressToOneHalf() {
        // Arrange
        JobExperience jobExperience = new JobExperience(
            "Miner",
            75,
            50,
            100,
            2
        );

        Player player = mock(Player.class);
        JobExperienceBar sut = new JobExperienceBar(player, null);

        // Act
        sut.setProgress(jobExperience);

        float actual = sut.getBossBar().progress();
        float expected = 0.5f;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setProgress_WithExpToNextLevel_ShouldSetProgressToOne() {
        // Arrange
        JobExperience jobExperience = new JobExperience(
            "Miner",
            100,
            50,
            100,
            2
        );

        Player player = mock(Player.class);
        JobExperienceBar sut = new JobExperienceBar(player, null);

        // Act
        sut.setProgress(jobExperience);

        float actual = sut.getBossBar().progress();
        float expected = 1f;

        // Assert
        assertEquals(expected, actual);
    }
}
