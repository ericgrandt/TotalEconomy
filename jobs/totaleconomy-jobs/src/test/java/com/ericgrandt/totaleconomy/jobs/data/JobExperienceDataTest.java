package com.ericgrandt.totaleconomy.jobs.data;

import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.common.testutils.TestUtils;
import com.ericgrandt.totaleconomy.jobs.dto.GetJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.model.JobExperience;
import com.ericgrandt.totaleconomy.jobs.testutils.TestSeeder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobExperienceDataTest {
    @Test
    @Tag("Integration")
    void getJobExperience_WithRowFound_ShouldReturnJobExperience() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var jobExperience = TestSeeder.seedJobExperience(dataSource);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.fromString(jobExperience.playerId());
        var getJobExperienceDto = new GetJobExperienceDto(playerId, "miner");

        var sut = new JobExperienceData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.getJobExperience(conn, getJobExperienceDto);
            var expected = Optional.of(new JobExperience(
                playerId,
                "miner",
                10
            ));

            assertEquals(expected, actual);
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void getJobExperience_WithNoRowFound_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.randomUUID();
        var getJobExperienceDto = new GetJobExperienceDto(playerId, "miner");

        var sut = new JobExperienceData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.getJobExperience(conn, getJobExperienceDto);
            var expected = Optional.empty();

            assertEquals(expected, actual);
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void upsertJobExperience_WithInsert_ShouldReturnJobExperience() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.randomUUID();
        var upsertJobExperienceDto = new UpsertJobExperienceDto(playerId, "miner", 10);

        var sut = new JobExperienceData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.upsertJobExperience(conn, upsertJobExperienceDto);
            var expected = new JobExperience(
                playerId,
                "miner",
                10
            );

            assertEquals(expected, actual);
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void upsertJobExperience_WithUpdate_ShouldReturnJobExperience() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var jobExperience = TestSeeder.seedJobExperience(dataSource);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.fromString(jobExperience.playerId());
        var upsertJobExperienceDto = new UpsertJobExperienceDto(playerId, "miner", 5);

        var sut = new JobExperienceData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.upsertJobExperience(conn, upsertJobExperienceDto);
            var expected = new JobExperience(
                playerId,
                "miner",
                15
            );

            assertEquals(expected, actual);
            return null;
        });
    }
}
