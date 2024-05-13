package com.ericgrandt.totaleconomy.common.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobRewardTest {
    @Test
    @Tag("Unit")
    public void getExperience_ShouldReturnExperience() {
        // Arrange
        JobReward sut = new JobReward("", "", "", 1, "", BigDecimal.ONE, 10);

        // Act
        int actual = sut.getExperience();
        int expected = 10;

        // Assert
        assertEquals(expected, actual);
    }
}
