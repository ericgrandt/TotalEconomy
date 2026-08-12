package com.ericgrandt.totaleconomy.jobs.data;

import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.common.testutils.TestUtils;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertActiveJobDto;
import com.ericgrandt.totaleconomy.jobs.model.ActiveJob;
import com.ericgrandt.totaleconomy.jobs.testutils.TestSeeder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActiveJobDataTest {
    @Test
    @Tag("Integration")
    void upsertActiveJob_WithInsert_ShouldReturnActiveJob() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.randomUUID();
        var upsertActiveJobDto = new UpsertActiveJobDto(playerId, "miner");

        var sut = new ActiveJobData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.upsertActiveJob(conn, upsertActiveJobDto);
            var expected = new ActiveJob(
                playerId,
                "miner"
            );

            assertEquals(expected, actual);
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void upsertActiveJob_WithUpdate_ShouldReturnActiveJob() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var playerActiveJob = TestSeeder.seedActiveJob(dataSource);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.fromString(playerActiveJob.playerId());
        var upsertActiveJobDto = new UpsertActiveJobDto(playerId, "lumberjack");

        var sut = new ActiveJobData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.upsertActiveJob(conn, upsertActiveJobDto);
            var expected = new ActiveJob(
                playerId,
                "lumberjack"
            );

            assertEquals(expected, actual);
            return null;
        });
    }
}
