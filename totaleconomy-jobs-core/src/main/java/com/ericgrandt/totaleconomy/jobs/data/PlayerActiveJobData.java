package com.ericgrandt.totaleconomy.jobs.data;

import com.ericgrandt.totaleconomy.jobs.dto.UpsertPlayerActiveJobDto;
import com.ericgrandt.totaleconomy.jobs.model.PlayerActiveJob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerActiveJobData {
    public PlayerActiveJob UpsertPlayerActiveJob(Connection conn, UpsertPlayerActiveJobDto req) throws SQLException {
        var insertQuery = "INSERT INTO te_player_active_job(player_id, job_id) VALUES (?, ?) AS alias ON DUPLICATE KEY UPDATE job_id = alias.job_id, joined_at = CURRENT_TIMESTAMP";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, req.playerId().toString());
            stmt.setString(2, req.jobId());
            stmt.executeUpdate();
        }
        return new PlayerActiveJob(req.playerId(), req.jobId());
    }
}
