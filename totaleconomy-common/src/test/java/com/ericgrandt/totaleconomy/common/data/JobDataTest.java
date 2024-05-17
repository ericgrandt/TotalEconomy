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
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetAllJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    public void getJobReward_WithRowFound_ShouldReturnAJobReward() throws SQLException {
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

        GetJobRewardRequest request = new GetJobRewardRequest("", "");
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward(request);
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

        GetJobRewardRequest request = new GetJobRewardRequest("", "");
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward(request);

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

        GetJobRewardRequest request = new GetJobRewardRequest("", "");
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward(request);

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getJob_WithRowFound_ShouldReturnJob() throws SQLException {
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

        GetJobRequest request = new GetJobRequest(UUID.randomUUID());
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(request);
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

        GetJobRequest request = new GetJobRequest(UUID.randomUUID());
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(request);

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

        GetJobRequest request = new GetJobRequest(UUID.randomUUID());
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(request);

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getJobExperience_WithRowFound_ShouldReturnJobExperience() throws SQLException {
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

        GetJobExperienceRequest request = new GetJobExperienceRequest(UUID.randomUUID(), UUID.randomUUID());
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(request);
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

        GetJobExperienceRequest request = new GetJobExperienceRequest(UUID.randomUUID(), UUID.randomUUID());
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(request);

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

        GetJobExperienceRequest request = new GetJobExperienceRequest(UUID.randomUUID(), UUID.randomUUID());
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(request);

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertTrue(actual.isEmpty());
    }

    @Test
    @Tag("Unit")
    public void getAllJobExperience_WithRowsFound_ShouldReturnListOfJobExperience() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("id")).thenReturn("id1").thenReturn("id2");
        when(resultSetMock.getString("account_id")).thenReturn("accountId1").thenReturn("accountId2");
        when(resultSetMock.getString("job_id")).thenReturn("jobId1").thenReturn("jobId2");
        when(resultSetMock.getInt("experience")).thenReturn(10).thenReturn(20);

        GetAllJobExperienceRequest request = new GetAllJobExperienceRequest(
            UUID.randomUUID()
        );
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        List<JobExperience> actual = sut.getAllJobExperience(request);
        List<JobExperience> expected = List.of(
            new JobExperience("id1", "accountId1", "jobId1", 10),
            new JobExperience("id2", "accountId2", "jobId2", 20)
        );

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Unit")
    public void getAllJobExperience_WithSQLException_ShouldReturnEmptyList() throws SQLException {
        // Arrange
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenThrow(SQLException.class);

        GetAllJobExperienceRequest request = new GetAllJobExperienceRequest(
            UUID.randomUUID()
        );
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        List<JobExperience> actual = sut.getAllJobExperience(request);
        List<JobExperience> expected = new ArrayList<>();

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
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

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            1
        );JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        int actual = sut.updateJobExperience(request);
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

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            1
        );
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        int actual = sut.updateJobExperience(request);
        int expected = 0;

        // Assert
        verify(loggerMock).error(any(String.class), any(SQLException.class));
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getJobReward_ShouldReturnAJobReward() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobActions();
        TestUtils.seedJobRewards();

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        GetJobRewardRequest request = new GetJobRewardRequest("break", "coal_ore");
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobReward> actual = sut.getJobReward(request);
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
    public void getJob_ShouldReturnJob() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedJobs();

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        GetJobRequest request = new GetJobRequest(UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1"));
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<Job> actual = sut.getJob(request);
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
    public void getJobExperience_ShouldReturnJobExperience() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        GetJobExperienceRequest request = new GetJobExperienceRequest(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"),
            UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1")
        );
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        Optional<JobExperience> actual = sut.getJobExperience(request);
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
    public void getAllJobExperience_ShouldReturnListOFJobExperience() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        GetAllJobExperienceRequest request = new GetAllJobExperienceRequest(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );
        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        List<JobExperience> actual = sut.getAllJobExperience(request);
        List<JobExperience> expected = List.of(
            new JobExperience(
                "748af95b-32a0-45c2-bfdc-9e87c023acdf",
                "62694fb0-07cc-4396-8d63-4f70646d75f0",
                "a56a5842-1351-4b73-a021-bcd531260cd1",
                50
            ),
            new JobExperience(
                "6cebc95b-7743-4f63-92c6-0fd0538d8b0c",
                "62694fb0-07cc-4396-8d63-4f70646d75f0",
                "858febd0-7122-4ea4-b270-a69a4b6a53a4",
                10
            )
        );

        // Assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @Tag("Integration")
    public void updateJobExperience_ShouldUpdateJobExperienceAndReturnOne() throws SQLException {
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

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.fromString(accountId),
            UUID.fromString(jobId),
            20
        );

        JobData sut = new JobData(loggerMock, databaseMock);

        // Act
        int actual = sut.updateJobExperience(request);
        int expected = 1;

        JobExperience actualJobExperience = sut.getJobExperience(
            new GetJobExperienceRequest(
                UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"),
                UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1")
            )
        ).orElseThrow();
        JobExperience expectedJobExperience = new JobExperience(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            "62694fb0-07cc-4396-8d63-4f70646d75f0",
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            70
        );

        // Assert
        assertEquals(expected, actual);
        assertThat(actualJobExperience).usingRecursiveComparison().isEqualTo(expectedJobExperience);
    }
}
