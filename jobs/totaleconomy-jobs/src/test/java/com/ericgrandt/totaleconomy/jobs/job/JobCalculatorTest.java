package com.ericgrandt.totaleconomy.jobs.job;

import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobCalculatorTest {
    private final Config config = new Config(
        List.of(
            new Config.Job(
                "miner",
                "Miner",
                "Mine ores",
                Map.of(
                    JobEnums.ActionType.BLOCK_BREAK, new Config.Job.Action(
                        JobEnums.ActionType.BLOCK_BREAK,
                        Map.of("coal_ore", new Config.Job.Action.Entry("COAL_ORE", 10, BigDecimal.ONE))
                    )
                )
            )
        ),
        new Config.Settings(
            100,
            new Config.Settings.XPCurve(JobEnums.XPCurveType.QUADRATIC, 100),
            new Config.Settings.PayoutMultiplier(JobEnums.PayoutMultiplierType.LINEAR, 1.0, 0.01)
        )
    );

    @Test
    @Tag("Unit")
    void getPayoutMultiplier_WithLevelGreaterThanOne_ShouldReturnCorrectValue() {
        // Arrange
        var sut = new JobCalculator(config);

        // Act
        var actual = sut.calculatePayout(3);
        var expected = 1.02;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    void getPayoutMultiplier_WithLevelLessThanOne_ShouldDefaultToLevelOneAndReturnCorrectValue() {
        // Arrange
        var sut = new JobCalculator(config);

        // Act
        var actual = sut.calculatePayout(0);
        var expected = 1.00;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    void calculateLevelFromExp_WithQuadraticCurve_ShouldReturnCorrectLevels() {
        // Arrange
        var sut = new JobCalculator(config);

        // Act/Assert
        assertEquals(1, sut.calculateLevelFromExp(0));
        assertEquals(2, sut.calculateLevelFromExp(100));
        assertEquals(50, sut.calculateLevelFromExp(249999));
        assertEquals(51, sut.calculateLevelFromExp(250000));
    }
}
