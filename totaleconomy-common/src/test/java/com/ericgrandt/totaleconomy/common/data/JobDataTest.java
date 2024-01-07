package com.ericgrandt.totaleconomy.common.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobRewardDto;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobDataTest {
    @Test
    @Tag("Unit")
    public void getJob_WithRowFound_ShouldReturnJobDto() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
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

        JobData sut = new JobData(databaseMock);

        // Act
        JobDto actual = sut.getJob(UUID.randomUUID());
        JobDto expected = new JobDto("id", "jobName");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getJob_WithNoRowFound_ShouldReturnNull() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(databaseMock);

        // Act
        JobDto actual = sut.getJob(UUID.randomUUID());

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getExperienceForJob_WithRowFound_ShouldReturnJobExperienceDto() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
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

        JobData sut = new JobData(databaseMock);

        // Act
        JobExperienceDto actual = sut.getExperienceForJob(UUID.randomUUID(), UUID.randomUUID());
        JobExperienceDto expected = new JobExperienceDto("id", "accountId", "jobId", 10);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getExperienceForJob_WithNoRowFound_ShouldReturnNull() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(databaseMock);

        // Act
        JobExperienceDto actual = sut.getExperienceForJob(UUID.randomUUID(), UUID.randomUUID());

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getExperienceForAllJobs_WithRowFound_ShouldReturnListOfJobExperienceDto() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("id")).thenReturn("id");
        when(resultSetMock.getString("account_id")).thenReturn("accountId");
        when(resultSetMock.getString("job_id")).thenReturn("jobId");
        when(resultSetMock.getInt("experience")).thenReturn(10);

        JobData sut = new JobData(databaseMock);

        // Act
        List<JobExperienceDto> actual = sut.getExperienceForAllJobs(UUID.randomUUID());
        List<JobExperienceDto> expected = List.of(
            new JobExperienceDto("id", "accountId", "jobId", 10)
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getExperienceForAllJobs_WithNoRowFound_ShouldReturnAnEmptyList() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(databaseMock);

        // Act
        List<JobExperienceDto> actual = sut.getExperienceForAllJobs(UUID.randomUUID());
        List<JobExperienceDto> expected = List.of();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithRowFound_ShouldReturnAJobRewardDto() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
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

        JobData sut = new JobData(databaseMock);

        // Act
        JobRewardDto actual = sut.getJobReward("", "");
        JobRewardDto expected = new JobRewardDto(
            "id",
            "jobId",
            "jobActionId",
            1,
            "material",
            BigDecimal.TEN,
            10
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithNoRowFound_ShouldReturnNull() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(databaseMock);

        // Act
        JobRewardDto actual = sut.getJobReward("", "");

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getJobActionByName_WithRowFound_ShouldReturnAJobActionDto() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("id")).thenReturn("id");
        when(resultSetMock.getString("action_name")).thenReturn("actionName");

        JobData sut = new JobData(databaseMock);

        // Act
        JobActionDto actual = sut.getJobActionByName("break");
        JobActionDto expected = new JobActionDto(
            "id",
            "actionName"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getJobActionByName_WithNoRowFound_ShouldReturnNull() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);

        JobData sut = new JobData(databaseMock);

        // Act
        JobActionDto actual = sut.getJobActionByName("break");

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void updateExperienceForJob_WithRowAffected_ShouldReturnOne() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        JobData sut = new JobData(databaseMock);

        // Act
        int actual = sut.updateExperienceForJob(UUID.randomUUID(), UUID.randomUUID(), 10);
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void updateExperienceForJob_WithNoRowsAffected_ShouldReturnZero() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0);

        JobData sut = new JobData(databaseMock);

        // Act
        int actual = sut.updateExperienceForJob(UUID.randomUUID(), UUID.randomUUID(), 10);
        int expected = 0;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getJob_ShouldReturnAJobDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedJobs();

        UUID jobId = UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        JobDto actual = sut.getJob(jobId);
        JobDto expected = new JobDto(
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            "Test Job 1"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getExperienceForJob_ShouldReturnAJobExperienceDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        UUID accountId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID jobId = UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        JobExperienceDto actual = sut.getExperienceForJob(accountId, jobId);
        JobExperienceDto expected = new JobExperienceDto(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            "62694fb0-07cc-4396-8d63-4f70646d75f0",
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            50
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getExperienceForAllJobs_ShouldReturnAListOfJobExperienceDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        UUID accountId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        List<JobExperienceDto> actual = sut.getExperienceForAllJobs(accountId);
        List<JobExperienceDto> expected = List.of(
            new JobExperienceDto(
                "748af95b-32a0-45c2-bfdc-9e87c023acdf",
                "62694fb0-07cc-4396-8d63-4f70646d75f0",
                "a56a5842-1351-4b73-a021-bcd531260cd1",
                50
            ),
            new JobExperienceDto(
                "6cebc95b-7743-4f63-92c6-0fd0538d8b0c",
                "62694fb0-07cc-4396-8d63-4f70646d75f0",
                "858febd0-7122-4ea4-b270-a69a4b6a53a4",
                10
            )
        );

        // Assert
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

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        JobRewardDto actual = sut.getJobReward("fbc60ff9-d7e2-4704-9460-6edc2e7b6066", "coal_ore");
        JobRewardDto expected = new JobRewardDto(
            "07ac5e1f-39ef-46a8-ad81-a4bc1facc090",
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            "fbc60ff9-d7e2-4704-9460-6edc2e7b6066",
            1,
            "coal_ore",
            BigDecimal.valueOf(0.50).setScale(2, RoundingMode.DOWN),
            1
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void getJobActionByName_ShouldReturnAJobExperienceDto() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobActions();

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        JobActionDto actual = sut.getJobActionByName("break");
        JobActionDto expected = new JobActionDto(
            "fbc60ff9-d7e2-4704-9460-6edc2e7b6066",
            "break"
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void createJobExperienceRows_ShouldCreateARowForEachJob() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();

        UUID accountId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        sut.createJobExperienceRows(accountId);

        List<JobExperienceDto> actual = TestUtils.getExperienceForJobs(accountId);
        List<JobExperienceDto> expected = Arrays.asList(
            new JobExperienceDto(
                "",
                accountId.toString(),
                "a56a5842-1351-4b73-a021-bcd531260cd1",
                0
            ),
            new JobExperienceDto(
                "",
                accountId.toString(),
                "858febd0-7122-4ea4-b270-a69a4b6a53a4",
                0
            )
        );

        // Assert
        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    @Tag("Integration")
    public void updateExperienceForJob_ShouldUpdateExperience() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        UUID accountId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID jobId = UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        int actual = sut.updateExperienceForJob(accountId, jobId, 60);
        int expected = 1;

        JobExperienceDto actualJobExperienceDto = TestUtils.getExperienceForJob(accountId, jobId);
        JobExperienceDto expectedJobExperienceDto = new JobExperienceDto(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            accountId.toString(),
            jobId.toString(),
            60
        );

        // Assert
        assertEquals(expected, actual);
        assertEquals(expectedJobExperienceDto, actualJobExperienceDto);
    }
}
