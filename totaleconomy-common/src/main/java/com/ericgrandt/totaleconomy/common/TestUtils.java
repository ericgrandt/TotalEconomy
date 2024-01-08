package com.ericgrandt.totaleconomy.common;

import com.ericgrandt.totaleconomy.common.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.common.data.dto.BalanceDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobExperienceDto;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.ibatis.jdbc.ScriptRunner;

public class TestUtils {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:h2:mem:totaleconomy;MODE=MySQL");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
        setupDb();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void setupDb() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try (
            InputStream is = classloader.getResourceAsStream("testSchema.sql");
            Connection conn = TestUtils.getConnection();
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(is))
        ) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.runScript(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void seedCurrencies() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertDollarCurrency = "INSERT INTO te_currency\n"
                + "VALUES(1, 'Dollar', 'Dollars', '$', 0, true)";

            Statement statement = conn.createStatement();
            statement.execute(insertDollarCurrency);
        }
    }

    public static void seedAccounts() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertAccount = "INSERT INTO te_account\n"
                + "VALUES('62694fb0-07cc-4396-8d63-4f70646d75f0', '2022-01-01 00:00:00');";
            String insertBalance = "INSERT INTO te_balance\n"
                + "VALUES('ab661384-11f5-41e1-a5e6-6fa93305d4d1', '62694fb0-07cc-4396-8d63-4f70646d75f0', 1, 50)";
            String insertAccount2 = "INSERT INTO te_account\n"
                + "VALUES('551fe9be-f77f-4bcb-81db-548db6e77aea', '2022-01-02 00:00:00');";
            String insertBalance2 = "INSERT INTO te_balance\n"
                + "VALUES('a766cedf-f53e-450d-804a-4f292357938f', '551fe9be-f77f-4bcb-81db-548db6e77aea', 1, 100)";

            Statement statement = conn.createStatement();
            statement.execute(insertAccount);
            statement.execute(insertBalance);
            statement.execute(insertAccount2);
            statement.execute(insertBalance2);
        }
    }

    public static void seedDefaultBalances() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertDefaultBalance = "INSERT INTO te_default_balance\n"
                + "VALUES('05231a59-b6fa-4d57-8450-5bd07f148a98', 1, 100.50);";

            Statement statement = conn.createStatement();
            statement.execute(insertDefaultBalance);
        }
    }

    public static void seedJobs() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertJob1 = "INSERT INTO te_job VALUES('a56a5842-1351-4b73-a021-bcd531260cd1', 'Test Job 1');";
            String insertJob2 = "INSERT INTO te_job VALUES('858febd0-7122-4ea4-b270-a69a4b6a53a4', 'Test Job 2');";

            Statement statement = conn.createStatement();
            statement.execute(insertJob1);
            statement.execute(insertJob2);
        }
    }

    public static void seedJobExperience() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertJobExperience1 = "INSERT INTO te_job_experience "
                + "VALUES('748af95b-32a0-45c2-bfdc-9e87c023acdf', '62694fb0-07cc-4396-8d63-4f70646d75f0', 'a56a5842-1351-4b73-a021-bcd531260cd1', 50);";
            String insertJobExperience2 = "INSERT INTO te_job_experience "
                + "VALUES('6cebc95b-7743-4f63-92c6-0fd0538d8b0c', '62694fb0-07cc-4396-8d63-4f70646d75f0', '858febd0-7122-4ea4-b270-a69a4b6a53a4', 10);";

            Statement statement = conn.createStatement();
            statement.execute(insertJobExperience1);
            statement.execute(insertJobExperience2);
        }
    }

    public static void seedJobActions() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertBreakAction = "INSERT INTO te_job_action "
                + "VALUES('fbc60ff9-d7e2-4704-9460-6edc2e7b6066', 'break');";
            String insertKillAction = "INSERT INTO te_job_action "
                + "VALUES('cd626a9a-b91e-48d2-8198-0952501f37c5', 'kill');";
            String insertFishAction = "INSERT INTO te_job_action "
                + "VALUES('7ac7daf8-88ad-45d2-b093-077270e3da75', 'fish');";
            String insertPlaceAction = "INSERT INTO te_job_action "
                + "VALUES('a65a654e-508a-47a5-87fa-cae5d04368a4', 'place');";

            Statement statement = conn.createStatement();
            statement.execute(insertBreakAction);
            statement.execute(insertKillAction);
            statement.execute(insertFishAction);
            statement.execute(insertPlaceAction);
        }
    }

    public static void seedJobRewards() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertBreakReward = "INSERT INTO te_job_reward "
                + "VALUES('07ac5e1f-39ef-46a8-ad81-a4bc1facc090', 'a56a5842-1351-4b73-a021-bcd531260cd1', "
                + "'fbc60ff9-d7e2-4704-9460-6edc2e7b6066', 1, 'coal_ore', 0.50, 1);";
            String insertKillReward = "INSERT INTO te_job_reward "
                + "VALUES('26ddbaab-3b3c-496c-b027-e2fe9b21ea5b', 'a56a5842-1351-4b73-a021-bcd531260cd1', "
                + "'cd626a9a-b91e-48d2-8198-0952501f37c5', 1, 'chicken', 1.00, 5);";
            String insertFishReward = "INSERT INTO te_job_reward "
                + "VALUES('c7642455-b14d-4125-9c01-6978a2169d15', 'a56a5842-1351-4b73-a021-bcd531260cd1', "
                + "'7ac7daf8-88ad-45d2-b093-077270e3da75', 1, 'salmon', 5.00, 20);";
            String insertPlaceReward = "INSERT INTO te_job_reward "
                + "VALUES('e24261c4-d131-457e-a2da-060170ed2633', 'a56a5842-1351-4b73-a021-bcd531260cd1', "
                + "'a65a654e-508a-47a5-87fa-cae5d04368a4', 1, 'oak_sapling', 0.01, 1);";

            Statement statement = conn.createStatement();
            statement.execute(insertBreakReward);
            statement.execute(insertKillReward);
            statement.execute(insertFishReward);
            statement.execute(insertPlaceReward);
        }
    }

    public static void resetDb() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String deleteCurrencies = "DELETE FROM te_currency";
            String deleteUsers = "DELETE FROM te_account";
            String deleteBalances = "DELETE FROM te_balance";
            String deleteDefaultBalances = "DELETE FROM te_default_balance";
            String deleteJobs = "DELETE FROM te_job";
            String deleteJobExperience = "DELETE FROM te_job_experience";
            String deleteJobActions = "DELETE FROM te_job_action";
            String deleteJobRewards = "DELETE FROM te_job_reward";

            Statement statement = conn.createStatement();
            statement.execute(deleteCurrencies);
            statement.execute(deleteUsers);
            statement.execute(deleteBalances);
            statement.execute(deleteDefaultBalances);
            statement.execute(deleteJobs);
            statement.execute(deleteJobExperience);
            statement.execute(deleteJobActions);
            statement.execute(deleteJobRewards);
        }
    }

    public static AccountDto getAccount(UUID accountId) throws SQLException {
        String query = "SELECT * FROM te_account WHERE id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new AccountDto(
                            rs.getString("id"),
                            rs.getTimestamp("created")
                        );
                    }

                    return new AccountDto("", Timestamp.valueOf("2000-01-01"));
                }
            }
        }
    }

    public static BalanceDto getBalanceForAccountId(UUID accountId, int currencyId) throws SQLException {
        String query = "SELECT * FROM te_balance WHERE account_id = ? AND currency_id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());
                stmt.setInt(2, currencyId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new BalanceDto(
                            rs.getString("id"),
                            rs.getString("account_id"),
                            rs.getInt("currency_id"),
                            rs.getBigDecimal("balance")
                        );
                    }

                    return new BalanceDto("", "", 0, BigDecimal.ONE);
                }
            }
        }
    }

    public static JobExperienceDto getExperienceForJob(UUID accountId, UUID jobId) throws SQLException {
        String query = "SELECT * FROM te_job_experience WHERE account_id = ? AND job_id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());
                stmt.setString(2, jobId.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new JobExperienceDto(
                            rs.getString("id"),
                            rs.getString("account_id"),
                            rs.getString("job_id"),
                            rs.getInt("experience")
                        );
                    }

                    return null;
                }
            }
        }
    }

    public static List<JobExperienceDto> getExperienceForJobs(UUID accountId) throws SQLException {
        String query = "SELECT account_id, job_id, experience FROM te_job_experience WHERE account_id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    List<JobExperienceDto> jobExperienceDtos = new ArrayList<>();
                    while (rs.next()) {
                        jobExperienceDtos.add(
                            new JobExperienceDto(
                                "",
                                rs.getString("account_id"),
                                rs.getString("job_id"),
                                rs.getInt("experience")
                            )
                        );
                    }

                    return jobExperienceDtos;
                }
            }
        }
    }
}
