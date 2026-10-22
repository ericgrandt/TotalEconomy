package com.ericgrandt.totaleconomy.jobs.data;

import com.ericgrandt.totaleconomy.jobs.dto.UpsertActiveJobDto;
import com.ericgrandt.totaleconomy.jobs.model.ActiveJob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class ActiveJobData {
    public Optional<ActiveJob> getActiveJob(Connection conn, UUID playerId) throws SQLException {
        var selectQuery = "SELECT player_id, job_id FROM te_active_job WHERE player_id = ?";
        try (var stmt = conn.prepareStatement(selectQuery)) {
            stmt.setString(1, playerId.toString());
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                        new ActiveJob(
                            UUID.fromString(rs.getString("player_id")),
                            rs.getString("job_id")
                        )
                    );
                }
            }
        }

        return Optional.empty();
    }

    public ActiveJob upsertActiveJob(Connection conn, UpsertActiveJobDto req) throws SQLException {
        // NOTE: Need to use VALUES(job_id) instead of "AS alias ... = alias.job_id" for H2 compatibility. Not ideal, though it works for now.
        var insertQuery = "INSERT INTO te_active_job(player_id, job_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE job_id = VALUES(job_id), joined_at = CURRENT_TIMESTAMP";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, req.playerId().toString());
            stmt.setString(2, req.jobId());
            stmt.executeUpdate();
        }
        return new ActiveJob(req.playerId(), req.jobId());
    }
}
