package com.ericgrandt.totaleconomy.jobs.testutils;

import com.ericgrandt.totaleconomy.jobs.data.entity.PlayerActiveJobEntity;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class TestSeeder {
    private static final String TEST_DATE = "2025-01-01T00:00:00Z";

    public static PlayerActiveJobEntity seedPlayerActiveJob(HikariDataSource dataSource) throws SQLException {
        PlayerActiveJobEntity playerActiveJob = new PlayerActiveJobEntity(
            1,
            UUID.randomUUID().toString(),
            "miner",
            Instant.parse(TEST_DATE)
        );
        String query = """
            INSERT IGNORE INTO te_player_active_job (
                player_id,
                job_id
            ) VALUES (?, ?)""";

        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerActiveJob.playerId());
                stmt.setString(2, playerActiveJob.jobId());
                stmt.execute();
            }
        }

        return playerActiveJob;
    }
}
