package com.ericgrandt.totaleconomy.common.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
