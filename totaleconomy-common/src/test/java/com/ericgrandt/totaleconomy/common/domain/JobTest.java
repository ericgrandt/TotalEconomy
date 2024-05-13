package com.ericgrandt.totaleconomy.common.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobTest {
    @Test
    @Tag("Unit")
    public void getJobName_ShouldReturnJobName() {
        // Arrange
        Job sut = new Job("id", "job");

        // Act
        String actual = sut.getJobName();
        String expected = "job";

        // Assert
        assertEquals(expected, actual);
    }
}
