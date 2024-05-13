package com.ericgrandt.totaleconomy.common.data;

import com.ericgrandt.totaleconomy.common.domain.Job;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class JobData {
    private final CommonLogger logger;
    private final Database database;

    public JobData(final CommonLogger logger, final Database database) {
        this.logger = logger;
        this.database = database;
    }

    public Optional<JobReward> getJobReward(String jobAction, String material) {
        String query = "SELECT tjr.* FROM te_job_reward tjr "
            + "INNER JOIN te_job_action tja ON "
            + "tja.action_name = ? "
            + "WHERE material = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, jobAction);
            stmt.setString(2, material);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                        new JobReward(
                            rs.getString("id"),
                            rs.getString("job_id"),
                            rs.getString("job_action_id"),
                            rs.getInt("currency_id"),
                            rs.getString("material"),
                            rs.getBigDecimal("money"),
                            rs.getInt("experience")
                        )
                    );
                }
            }
        } catch (SQLException e) {
            logger.error(
                "[TotalEconomy] Error querying the database",
                e
            );
        }

        return Optional.empty();
    }

    public Optional<Job> getJob(UUID jobId) {
        String query = "SELECT * FROM te_job WHERE id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, jobId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                        new Job(
                            rs.getString("id"),
                            rs.getString("job_name")
                        )
                    );
                }
            }
        } catch (SQLException e) {
            logger.error(
                "[TotalEconomy] Error querying the database",
                e
            );
        }

        return Optional.empty();
    }

    public Optional<JobExperience> getJobExperience(UUID accountId, UUID jobId) {
        String query = "SELECT * FROM te_job_experience WHERE account_id = ? AND job_id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, accountId.toString());
            stmt.setString(2, jobId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                        new JobExperience(
                            rs.getString("id"),
                            rs.getString("account_id"),
                            rs.getString("job_id"),
                            rs.getInt("experience")
                        )
                    );
                }
            }
        } catch (SQLException e) {
            logger.error(
                "[TotalEconomy] Error querying the database",
                e
            );
        }

        return Optional.empty();
    }
}
