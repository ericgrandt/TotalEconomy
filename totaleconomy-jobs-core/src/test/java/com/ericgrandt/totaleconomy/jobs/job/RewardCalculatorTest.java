package com.ericgrandt.totaleconomy.jobs.job;

import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RewardCalculatorTest {
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
        var sut = new RewardCalculator(config);

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
        var sut = new RewardCalculator(config);

        // Act
        var actual = sut.calculatePayout(0);
        var expected = 1.00;

        // Assert
        assertEquals(expected, actual);
    }


    @Test
    @Tag("Unit")
    void getEntry_WithValidPath_ShouldReturnCorrectValue() {
        // Arrange
        var jobName = "miner";
        var actionType = JobEnums.ActionType.BLOCK_BREAK;
        var materialName = "coal_ore";

        var sut = new RewardCalculator(config);

        // Act
        var actual = sut.getEntry(jobName, actionType, materialName);
        var expected = Optional.of(new Config.Job.Action.Entry("COAL_ORE", 10, BigDecimal.ONE));

        // Assert
        assertTrue(actual.isPresent());
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    void getEntry_WithInvalidJobName_ShouldReturnEmptyOptional() {
        // Arrange
        var jobName = "notAJob";
        var actionType = JobEnums.ActionType.BLOCK_BREAK;
        var materialName = "coal_ore";

        var sut = new RewardCalculator(config);

        // Act
        var actual = sut.getEntry(jobName, actionType, materialName);
        var expected = Optional.empty();

        // Assert
        assertTrue(actual.isEmpty());
        assertEquals(expected, actual);
    }

    //@Test
    //@Tag("Unit")
    //void getEntry_WithInvalidActionName_ShouldReturnEmptyOptional() {
    //    // Arrange
    //    var jobName = "miner";
    //    var actionName = "NOT_AN_ACTION";
    //    var materialName = "coal_ore";

    //    var sut = new RewardCalculator(config);

    //    // Act
    //    var actual = sut.getEntry(jobName, actionName, materialName);
    //    var expected = Optional.empty();

    //    // Assert
    //    assertTrue(actual.isEmpty());
    //    assertEquals(expected, actual);
    //}

    @Test
    @Tag("Unit")
    void getEntry_WithInvalidMaterialName_ShouldReturnEmptyOptional() {
        // Arrange
        var jobName = "miner";
        var actionType = JobEnums.ActionType.BLOCK_BREAK;
        var materialName = "not_a_material";

        var sut = new RewardCalculator(config);

        // Act
        var actual = sut.getEntry(jobName, actionType, materialName);
        var expected = Optional.empty();

        // Assert
        assertTrue(actual.isEmpty());
        assertEquals(expected, actual);
    }
}
