package com.ericgrandt.totaleconomy.jobs.data;

import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.common.testutils.TestUtils;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertPlayerActiveJobDto;
import com.ericgrandt.totaleconomy.jobs.model.PlayerActiveJob;
import com.ericgrandt.totaleconomy.jobs.testutils.TestSeeder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerActiveJobDataTest {
    @Test
    @Tag("Integration")
    void upsertPlayerActiveJob_WithInsert_ShouldReturnPlayerActiveJob() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.randomUUID();
        var upsertPlayerActiveJobDto = new UpsertPlayerActiveJobDto(playerId, "miner");

        var sut = new PlayerActiveJobData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.upsertPlayerActiveJob(conn, upsertPlayerActiveJobDto);
            var expected = new PlayerActiveJob(
                playerId,
                "miner"
            );

            assertEquals(expected, actual);
            return null;
        });
    }

    @Test
    @Tag("Integration")
    void upsertPlayerActiveJob_WithUpdate_ShouldReturnPlayerActiveJob() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var playerActiveJob = TestSeeder.seedPlayerActiveJob(dataSource);
        var util = new TransactionUtil(dataSource);

        var playerId = UUID.fromString(playerActiveJob.playerId());
        var upsertPlayerActiveJobDto = new UpsertPlayerActiveJobDto(playerId, "lumberjack");

        var sut = new PlayerActiveJobData();

        // Act/Assert
        util.runInTransaction(conn -> {
            var actual = sut.upsertPlayerActiveJob(conn, upsertPlayerActiveJobDto);
            var expected = new PlayerActiveJob(
                playerId,
                "lumberjack"
            );

            assertEquals(expected, actual);
            return null;
        });
    }
}
