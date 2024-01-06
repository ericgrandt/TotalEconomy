package com.ericgrandt.totaleconomy.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.models.AddExperienceResult;
import com.ericgrandt.totaleconomy.models.JobExperience;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void getJobReward_WithRewardFound_ShouldReturnJobRewardDto() throws SQLException {
        // Arrange
        JobActionDto jobAction = new JobActionDto("id", "break");
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenReturn(jobAction);
        when(jobDataMock.getJobReward(jobAction.id(), jobReward.material())).thenReturn(jobReward);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobRewardDto actual = sut.getJobReward("break", jobReward.material());

        // Assert
        assertEquals(jobReward, actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithNoJobActionFound_ShouldReturnNull() throws SQLException {
        // Arrange
        JobActionDto jobAction = new JobActionDto("id", "break");
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenReturn(null);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobRewardDto actual = sut.getJobReward("break", jobReward.material());

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithNoRewardFound_ShouldReturnNull() throws SQLException {
        // Arrange
        JobActionDto jobAction = new JobActionDto("id", "break");
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenReturn(jobAction);
        when(jobDataMock.getJobReward(jobAction.id(), jobReward.material())).thenReturn(null);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobRewardDto actual = sut.getJobReward("break", jobReward.material());

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithSqlException_ShouldReturnNull() throws SQLException {
        // Arrange
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenThrow(SQLException.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobRewardDto actual = sut.getJobReward("break", jobReward.material());

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        JobActionDto jobAction = new JobActionDto("id", "break");
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenReturn(jobAction);
        when(jobDataMock.getJobReward(jobAction.id(), jobReward.material())).thenThrow(SQLException.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.getJobReward("break", jobReward.material());

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(
                String.format(
                    "[Total Economy] Error calling getJobReward (actionName: %s, materialName: %s)",
                    "break",
                    jobReward.material()
                )
            ),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void getExperienceForJob_WithSuccess_ShouldReturnJobExperience() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobExperienceDto jobExperienceDto = new JobExperienceDto(
            "id",
            accountId.toString(),
            jobId.toString(),
            10
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(jobExperienceDto);
        when(jobDataMock.getJob(jobId)).thenReturn(
            new JobDto(jobId.toString(), "jobName")
        );

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobExperience actual = sut.getExperienceForJob(accountId, jobId);
        JobExperience expected = new JobExperience("jobName", 10, 0, 50, 1);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getExperienceForAllJobs_WithSuccess_ShouldReturnListOfJobExperienceRecords() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForAllJobs(accountId)).thenReturn(
            List.of(
                new JobExperienceDto("id", accountId.toString(), jobId.toString(), 10)
            )
        );
        when(jobDataMock.getJob(jobId)).thenReturn(
            new JobDto(jobId.toString(), "jobName")
        );

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        List<JobExperience> actual = sut.getExperienceForAllJobs(accountId);
        List<JobExperience> expected = List.of(
            new JobExperience("jobName", 10, 0, 50, 1)
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void createJobExperienceForAccount_WithSuccess_ShouldCallJobData() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();

        JobData jobDataMock = mock(JobData.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.createJobExperienceForAccount(accountId);

        // Assert
        verify(jobDataMock, times(1)).createJobExperienceRows(accountId);
    }

    @Test
    @Tag("Unit")
    public void createJobExperienceForAccount_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();

        JobData jobDataMock = mock(JobData.class);
        doThrow(SQLException.class).when(jobDataMock).createJobExperienceRows(accountId);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.createJobExperienceForAccount(accountId);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(
                String.format(
                    "[Total Economy] Error calling createJobExperienceForAccount (accountId: %s)",
                    accountId
                )
            ),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithJobExperienceFoundForAccount_ShouldUpdateExperience() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobExperienceDto jobExperienceDto = new JobExperienceDto(
            "id",
            accountId.toString(),
            jobId.toString(),
            10
        );
        JobDto jobDto = new JobDto(jobId.toString(), "Test Job 1");
        int experienceToAdd = 1;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(jobExperienceDto);
        when(jobDataMock.getJob(jobId)).thenReturn(jobDto);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.addExperience(accountId, jobId, experienceToAdd);

        // Assert
        verify(jobDataMock, times(1)).updateExperienceForJob(
            accountId,
            jobId,
            jobExperienceDto.experience() + experienceToAdd
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithJobExperienceFoundForAccountAndNoLevelUp_ShouldReturnProperResponse() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobExperienceDto jobExperienceDto = new JobExperienceDto(
            "id",
            accountId.toString(),
            jobId.toString(),
            10
        );
        JobDto jobDto = new JobDto(jobId.toString(), "Test Job 1");
        int experienceToAdd = 1;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(jobExperienceDto);
        when(jobDataMock.getJob(jobId)).thenReturn(jobDto);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        AddExperienceResult actual = sut.addExperience(accountId, jobId, experienceToAdd);
        AddExperienceResult expected = new AddExperienceResult(
            new JobExperience(jobDto.jobName(), 11, 0, 50, 1),
            false
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithJobExperienceFoundForAccountAndLevelUp_ShouldReturnProperResponse() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobExperienceDto jobExperienceDto = new JobExperienceDto(
            "id",
            accountId.toString(),
            jobId.toString(),
            10
        );
        JobDto jobDto = new JobDto(jobId.toString(), "Test Job 1");
        int experienceToAdd = 100;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(jobExperienceDto);
        when(jobDataMock.getJob(jobId)).thenReturn(jobDto);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        AddExperienceResult actual = sut.addExperience(accountId, jobId, experienceToAdd);
        AddExperienceResult expected = new AddExperienceResult(
            new JobExperience(jobDto.jobName(), 110, 50, 197, 2),
            true
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithJobExperienceNotFoundForAccount_ShouldReturnProperResponse() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        int experienceToAdd = 100;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(null);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        AddExperienceResult actual = sut.addExperience(accountId, jobId, experienceToAdd);
        AddExperienceResult expected = new AddExperienceResult(
            null,
            false
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithSQLException_ShouldLogError() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobExperienceDto jobExperienceDto = new JobExperienceDto(
            "id",
            accountId.toString(),
            jobId.toString(),
            10
        );
        int experienceToAdd = 100;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(jobExperienceDto);
        when(
            jobDataMock.updateExperienceForJob(
                accountId,
                jobId,
                jobExperienceDto.experience() + experienceToAdd
            )
        ).thenThrow(SQLException.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.addExperience(accountId, jobId, experienceToAdd);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling addExperience (accountId: %s, jobId: %s, experienceToAdd: %s)",
                accountId,
                jobId,
                experienceToAdd
            )),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithSQLException_ShouldReturnProperResponse() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        JobExperienceDto jobExperienceDto = new JobExperienceDto(
            "id",
            accountId.toString(),
            jobId.toString(),
            10
        );
        int experienceToAdd = 100;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(jobExperienceDto);
        when(
            jobDataMock.updateExperienceForJob(
                accountId,
                jobId,
                jobExperienceDto.experience() + experienceToAdd
            )
        ).thenThrow(SQLException.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        AddExperienceResult actual = sut.addExperience(accountId, jobId, experienceToAdd);
        AddExperienceResult expected = new AddExperienceResult(null, false);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void calculateLevelFromExperience_WithExperienceOf49_ShouldReturnOne() {
        // Arrange
        JobService sut = new JobService(loggerMock, mock(JobData.class));

        // Act
        int actual = sut.calculateLevelFromExperience(49);
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void calculateLevelFromExperience_WithExperienceOf50_ShouldReturnTwo() {
        // Arrange
        JobService sut = new JobService(loggerMock, mock(JobData.class));

        // Act
        int actual = sut.calculateLevelFromExperience(50);
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void calculateLevelFromExperience_WithExperienceOf4900_ShouldReturnTen() {
        // Arrange
        JobService sut = new JobService(loggerMock, mock(JobData.class));

        // Act
        int actual = sut.calculateLevelFromExperience(4900);
        int expected = 10;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void calculateLevelFromExperience_WithExperienceOfZero_ShouldReturnOne() {
        // Arrange
        JobService sut = new JobService(loggerMock, mock(JobData.class));

        // Act
        int actual = sut.calculateLevelFromExperience(0);
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }
}
