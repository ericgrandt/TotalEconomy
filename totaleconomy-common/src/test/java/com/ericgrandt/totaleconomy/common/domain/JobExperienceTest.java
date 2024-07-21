package com.ericgrandt.totaleconomy.common.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class JobExperienceTest {
    @Test
    @Tag("Unit")
    public void getLevel_ShouldReturnCorrectLevelForExperience() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 100);

        // Act
        int actual = sut.getLevel();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getLevel_WithNoExperience_ShouldReturnOne() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 0);

        // Act
        int actual = sut.getLevel();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getNextLevelExperience_WithACurrentLevelOfOne_ShouldReturnExperienceForLevelTwo() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 25);

        // Act
        int actual = sut.getNextLevelExperience();
        int expected = 50;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getNextLevelExperience_WithACurrentLevelOfTwo_ShouldReturnExperienceForLevelThree() {
        // Arrange
        JobExperience sut = new JobExperience("", "", "", 50);

        // Act
        int actual = sut.getNextLevelExperience();
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
    public void getJobId_ShouldReturnJobId() {
        // Arrange
        JobExperience sut = new JobExperience("id", "", "jobId", 0);

        // Act
        String actual = sut.getJobId();
        String expected = "jobId";

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
