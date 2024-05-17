package com.ericgrandt.totaleconomy.common.data;

import com.ericgrandt.totaleconomy.common.domain.Job;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.CreateJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetAllJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobData {
    private final CommonLogger logger;
    private final Database database;

    public JobData(final CommonLogger logger, final Database database) {
        this.logger = logger;
        this.database = database;
    }

    public Optional<JobReward> getJobReward(GetJobRewardRequest request) {
        String query = "SELECT tjr.* FROM te_job_reward tjr "
            + "INNER JOIN te_job_action tja ON "
            + "tja.action_name = ? "
            + "WHERE material = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, request.action());
            stmt.setString(2, request.material());

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

    public Optional<Job> getJob(GetJobRequest request) {
        String query = "SELECT * FROM te_job WHERE id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, request.jobId().toString());

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

    public Optional<JobExperience> getJobExperience(GetJobExperienceRequest request) {
        String query = "SELECT * FROM te_job_experience WHERE account_id = ? AND job_id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, request.accountId().toString());
            stmt.setString(2, request.jobId().toString());

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

    public List<JobExperience> getAllJobExperience(GetAllJobExperienceRequest request) {
        String query = "SELECT * FROM te_job_experience WHERE account_id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, request.accountId().toString());

            List<JobExperience> jobExperienceList = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    jobExperienceList.add(
                        new JobExperience(
                            rs.getString("id"),
                            rs.getString("account_id"),
                            rs.getString("job_id"),
                            rs.getInt("experience")
                        )
                    );
                }
            }

            return jobExperienceList;
        } catch (SQLException e) {
            logger.error(
                "[TotalEconomy] Error querying the database",
                e
            );
        }

        return new ArrayList<>();
    }

    public int updateJobExperience(AddExperienceRequest request) {
        String query = "UPDATE te_job_experience SET experience = experience + ? WHERE account_id = ? AND job_id = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement updateExperience = conn.prepareStatement(query)
        ) {
            updateExperience.setInt(1, request.experience());
            updateExperience.setString(2, request.accountId().toString());
            updateExperience.setString(3, request.jobId().toString());

            return updateExperience.executeUpdate();
        } catch (SQLException e) {
            logger.error(
                "[TotalEconomy] Error querying the database",
                e
            );
        }

        return 0;
    }

    public void createJobExperience(CreateJobExperienceRequest request) {

    }
}
