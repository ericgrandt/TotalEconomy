package com.ericgrandt.totaleconomy.common.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class JobExperienceTest {
    @Test
    @Tag("Unit")
    public void level_ShouldReturnCorrectLevelForExperience() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 100);

        // Act
        int actual = sut.level();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void level_WithNoExperience_ShouldReturnOne() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 0);

        // Act
        int actual = sut.level();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void nextLevelExperience_WithACurrentLevelOfOne_ShouldReturnExperienceForLevelTwo() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 25);

        // Act
        int actual = sut.nextLevelExperience();
        int expected = 50;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void nextLevelExperience_WithACurrentLevelOfTwo_ShouldReturnExperienceForLevelThree() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 50);

        // Act
        int actual = sut.nextLevelExperience();
        int expected = 197;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithExactlyEnoughExperienceToLevel_ShouldReturnTrue() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 45);

        // Act
        boolean actual = sut.addExperience(5);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithEnoughExperienceToLevel_ShouldReturnTrue() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 45);

        // Act
        boolean actual = sut.addExperience(10);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithoutEnoughExperienceToLevel_ShouldReturnFalse() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 45);

        // Act
        boolean actual = sut.addExperience(1);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getId_ShouldReturnId() {
        // Arrange
        JobExperience sut = new JobExperience("id", "", "", 0);

        // Act
        String actual = sut.getId();
        String expected = "id";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getExperience_ShouldReturnExperience() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 10);

        // Act
        int actual = sut.getExperience();
        int expected = 10;

        // Assert
        assertEquals(expected, actual);
    }
}
