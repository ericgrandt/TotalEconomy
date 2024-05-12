package com.ericgrandt.totaleconomy.common.data;

import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JobData {
    private final CommonLogger logger;
    private final Database database;

    public JobData(final CommonLogger logger, final Database database) {
        this.logger = logger;
        this.database = database;
    }

    public Optional<JobReward> getJobReward(String jobActionId, String material) {
        String getDefaultBalanceQuery = "SELECT * FROM te_job_reward "
            + "WHERE job_action_id = ? AND material = ?";

        try (
            Connection conn = database.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
            stmt.setString(1, jobActionId);
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
}
