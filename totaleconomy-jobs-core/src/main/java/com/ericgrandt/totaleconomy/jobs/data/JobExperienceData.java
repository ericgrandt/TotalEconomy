package com.ericgrandt.totaleconomy.jobs.data;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.jobs.dto.GetJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.model.JobExperience;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class JobExperienceData {
    public Optional<JobExperience> getJobExperience(Connection conn, GetJobExperienceDto req) throws SQLException {
        var selectQuery = "SELECT player_id, job_id, xp FROM te_job_experience WHERE player_id = ? AND job_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
            stmt.setString(1, req.playerId().toString());
            stmt.setString(2, req.jobId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                        new JobExperience(
                            UUID.fromString(rs.getString("player_id")),
                            rs.getString("job_id"),
                            rs.getInt("xp")
                        )
                    );
                }
            }
        }

        return Optional.empty();
    }

    public JobExperience upsertJobExperience(Connection conn, UpsertJobExperienceDto req) throws SQLException {
        var insertQuery = "INSERT INTO te_job_experience(player_id, job_id, xp) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE xp = VALUES(xp) + xp";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, req.playerId().toString());
            stmt.setString(2, req.jobId());
            stmt.setInt(3, req.xpToAdd());
            stmt.executeUpdate();
        }
        return getJobExperience(conn, new GetJobExperienceDto(req.playerId(), req.jobId()))
            .orElseThrow(() -> new DatabaseException("row missing after job experience upsert for player: %s, jobId: %s".formatted(
                req.playerId(),
                req.jobId()
            )));
    }
}
