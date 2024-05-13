package com.ericgrandt.totaleconomy.common.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.domain.Job;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobDataTest {
    @Mock
    private CommonLogger loggerMock;

    @Mock
    private Database databaseMock;

    @Test
    @Tag("Unit")
    public void getJobReward_WithRowFound_ShouldReturnAJobRewardDto() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("id")).thenReturn("id");
        when(resultSetMock.getString("job_id")).thenReturn("jobId");
        when(resultSetMock.getString("job_action_id")).thenReturn("jobActionId");
        when(resultSetMock.getInt("currency_id")).thenReturn(1);
        when(resultSetMock.getString("material")).thenReturn("material");
        when(resultSetMock.getBigDecimal("money")).thenReturn(BigDecimal.TEN);
        when(resultSetMock.getInt("experience")).thenReturn(10);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward("", "");
        JobReward expected = new JobReward(
            "id",
            "jobId",
            "jobActionId",
            1,
            "material",
            BigDecimal.TEN,
            10
        );

        // Assert
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithNoRowFound_ShouldReturnNull() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward("", "");

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithSQLException_ShouldReturnNull() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenThrow(SQLException.class);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward("", "");

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getJob_WithRowFound_ShouldReturnJobDto() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("id")).thenReturn("id");
        when(resultSetMock.getString("job_name")).thenReturn("jobName");

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(UUID.randomUUID());
        Job expected = new Job("id", "jobName");

        // Assert
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Unit")
    public void getJob_WithNoRowFound_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(UUID.randomUUID());

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getJob_WithSQLException_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenThrow(SQLException.class);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(UUID.randomUUID());

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getJobExperience_WithRowFound_ShouldReturnJobExperienceDto() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("id")).thenReturn("id");
        when(resultSetMock.getString("account_id")).thenReturn("accountId");
        when(resultSetMock.getString("job_id")).thenReturn("jobId");
        when(resultSetMock.getInt("experience")).thenReturn(10);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(UUID.randomUUID(), UUID.randomUUID());
        JobExperience expected = new JobExperience("id", "accountId", "jobId", 10);

        // Assert
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Unit")
    public void getJobExperience_WithNoRowFound_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(UUID.randomUUID(), UUID.randomUUID());

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getJobExperience_WithSQLException_ShouldReturnEmptyOptional() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenThrow(SQLException.class);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(UUID.randomUUID(), UUID.randomUUID());

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertTrue(actual.isEmpty());
    }


    @Test
    @Tag("Unit")
    public void updateJobExperience_WithRowUpdated_ShouldReturnOne() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        JobExperience jobExperience = new JobExperience("id", "accountId", "jobId", 10);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        int actual = sut.updateJobExperience(jobExperience);
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void updateJobExperience_WithSQLException_ShouldReturnZero() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenThrow(SQLException.class);

        JobExperience jobExperience = new JobExperience("id", "accountId", "jobId", 10);

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        int actual = sut.updateJobExperience(jobExperience);
        int expected = 0;

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getJobReward_ShouldReturnAJobRewardDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobActions();
        TestUtils.seedJobRewards();

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward("break", "coal_ore");
        JobReward expected = new JobReward(
            "07ac5e1f-39ef-46a8-ad81-a4bc1facc090",
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            "fbc60ff9-d7e2-4704-9460-6edc2e7b6066",
            1,
            "coal_ore",
            BigDecimal.valueOf(0.50).setScale(2, RoundingMode.DOWN),
            1
        );

        // Assert
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Integration")
    public void getJob_ShouldReturnAJobDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedJobs();

        UUID jobId = UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1");

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(jobId);
        Job expected = new Job(
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            "Test Job 1"
        );

        // Assert
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Integration")
    public void getJobExperience_ShouldReturnAJobExperienceDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        UUID accountId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID jobId = UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1");

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(accountId, jobId);
        JobExperience expected = new JobExperience(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            "62694fb0-07cc-4396-8d63-4f70646d75f0",
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            50
        );

        // Assert
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Integration")
    public void updateJobExperience_ShouldReturnUpdatedJobExperienceAndReturnOne() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());

        String accountId = "62694fb0-07cc-4396-8d63-4f70646d75f0";
        String jobId = "a56a5842-1351-4b73-a021-bcd531260cd1";
        JobExperience jobExperience = new JobExperience(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            accountId,
            jobId,
            100
        );

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        int actual = sut.updateJobExperience(jobExperience);
        int expected = 1;

        JobExperience actualJobExperience = sut.getJobExperience(UUID.fromString(accountId), UUID.fromString(jobId))
            .orElseThrow();

        // Assert
        assertThat(actualJobExperience).usingRecursiveComparison().isEqualTo(jobExperience);
        assertEquals(expected, actual);
    }
}
