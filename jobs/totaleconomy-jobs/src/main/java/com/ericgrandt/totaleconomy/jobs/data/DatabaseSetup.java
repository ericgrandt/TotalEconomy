package com.ericgrandt.totaleconomy.jobs.data;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void init(Connection conn) throws SQLException {
        createActiveJobTable(conn);
        createJobExperienceTable(conn);
    }

    private static void createActiveJobTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS te_active_job (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                player_id VARCHAR(36) NOT NULL,
                job_id VARCHAR(50) NOT NULL,
                joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                UNIQUE KEY uk_te_active_job_player (player_id)
            )
            """;

        try (var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void createJobExperienceTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS te_job_experience (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                player_id VARCHAR(36) NOT NULL,
                job_id VARCHAR(50) NOT NULL,
                xp BIGINT NOT NULL DEFAULT 0,
                UNIQUE KEY uk_te_job_experience_player_job (player_id, job_id)
            )
            """;

        try (var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
