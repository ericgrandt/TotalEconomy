package com.ericgrandt.totaleconomy.jobs.testutils;

import com.ericgrandt.totaleconomy.jobs.data.entity.ActiveJobEntity;
import com.ericgrandt.totaleconomy.jobs.data.entity.JobExperienceEntity;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class TestSeeder {
    private static final String TEST_DATE = "2025-01-01T00:00:00Z";

    public static ActiveJobEntity seedActiveJob(HikariDataSource dataSource) throws SQLException {
        var activeJob = new ActiveJobEntity(
            1,
            UUID.randomUUID().toString(),
            "miner",
            Instant.parse(TEST_DATE)
        );
        var query = """
            INSERT IGNORE INTO te_active_job (
                player_id,
                job_id
            ) VALUES (?, ?)""";

        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, activeJob.playerId());
                stmt.setString(2, activeJob.jobId());
                stmt.execute();
            }
        }

        return activeJob;
    }

    public static JobExperienceEntity seedJobExperience(HikariDataSource dataSource) throws SQLException {
        var jobExperience = new JobExperienceEntity(
            1,
            UUID.randomUUID().toString(),
            "miner",
            10
        );
        var query = """
            INSERT IGNORE INTO te_job_experience (
                player_id,
                job_id,
                xp
            ) VALUES (?, ?, ?)""";

        try (Connection conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, jobExperience.playerId());
                stmt.setString(2, jobExperience.jobId());
                stmt.setInt(3, jobExperience.xp());
                stmt.execute();
            }
        }

        return jobExperience;
    }
}
