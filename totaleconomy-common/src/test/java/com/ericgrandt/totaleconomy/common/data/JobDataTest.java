package com.ericgrandt.totaleconomy.common.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.domain.Job;
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
}
