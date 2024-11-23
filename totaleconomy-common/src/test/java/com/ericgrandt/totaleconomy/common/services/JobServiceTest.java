package com.ericgrandt.totaleconomy.common.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.domain.Job;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.dto.ExperienceBarDto;
import com.ericgrandt.totaleconomy.common.jobs.ExperienceBar;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.CreateJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetAllJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceResponse;
import com.ericgrandt.totaleconomy.common.models.GetJobRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private JobData jobDataMock;

    private JobService sut;

    @BeforeEach
    public void setUp() {
        sut = new JobService(jobDataMock);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithSuccess_ShouldReturnGetJobRewardResponse() {
        // Arrange
        JobReward jobReward = new JobReward("", "", "", 1, "", BigDecimal.ONE, 30);
        when(jobDataMock.getJobReward(any(GetJobRewardRequest.class)))
            .thenReturn(Optional.of(jobReward));

        GetJobRewardRequest request = new GetJobRewardRequest("", "");

        // Act
        GetJobRewardResponse actual = sut.getJobReward(request);
        GetJobRewardResponse expected = new GetJobRewardResponse("", BigDecimal.ONE, 30);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithEmptyJobReward_ShouldThrowNoSuchElementException() {
        // Arrange
        when(jobDataMock.getJobReward(any(GetJobRewardRequest.class)))
            .thenReturn(Optional.empty());

        GetJobRewardRequest request = new GetJobRewardRequest("", "");

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.getJobReward(request)
        );
    }

    @Test
    @Tag("Unit")
    public void getExperienceBarDto_WithSuccess_ShouldReturnExperienceBarDto() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        Optional<JobExperience> jobExperienceOpt = Optional.of(
            new JobExperience("id1", "accountId1", jobId.toString(), 1)
        );
        Job job = new Job(jobId.toString(), "jobName1");
        when(jobDataMock.getJobExperience(any(GetJobExperienceRequest.class)))
            .thenReturn(jobExperienceOpt);
        when(jobDataMock.getJob(any(GetJobRequest.class))).thenReturn(Optional.of(job));

        GetJobExperienceRequest request = new GetJobExperienceRequest(
            UUID.randomUUID(),
            jobId
        );

        // Act
        ExperienceBarDto actual = sut.getExperienceBarDto(request);
        ExperienceBarDto expected = new ExperienceBarDto("jobName1", 1, 50, 0);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getExperienceBarDto_WithEmptyJobExperience_ShouldThrowNoSuchElementException() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        when(jobDataMock.getJobExperience(any(GetJobExperienceRequest.class)))
            .thenReturn(Optional.empty());

        GetJobExperienceRequest request = new GetJobExperienceRequest(
            UUID.randomUUID(),
            jobId
        );

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.getExperienceBarDto(request)
        );
    }

    @Test
    @Tag("Unit")
    public void getExperienceBarDto_WithEmptyJob_ShouldThrowNoSuchElementException() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        Optional<JobExperience> jobExperienceOpt = Optional.of(
            new JobExperience("id1", "accountId1", jobId.toString(), 1)
        );
        Job job = new Job(jobId.toString(), "jobName1");
        when(jobDataMock.getJobExperience(any(GetJobExperienceRequest.class)))
            .thenReturn(jobExperienceOpt);
        when(jobDataMock.getJob(any(GetJobRequest.class))).thenReturn(Optional.empty());

        GetJobExperienceRequest request = new GetJobExperienceRequest(
            UUID.randomUUID(),
            jobId
        );

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.getExperienceBarDto(request)
        );
    }

    @Test
    @Tag("Unit")
    public void getAllJobExperience_WithSuccess_ShouldReturnListOfGetJobExperienceResponse() {
        // Arrange
        String jobId = UUID.randomUUID().toString();
        List<JobExperience> jobExperienceList = List.of(
            new JobExperience("id1", "accountId1", jobId, 1)
        );
        Job job = new Job(jobId, "jobName1");
        when(jobDataMock.getAllJobExperience(any(GetAllJobExperienceRequest.class)))
            .thenReturn(jobExperienceList);
        when(jobDataMock.getJob(any(GetJobRequest.class))).thenReturn(Optional.of(job));

        GetAllJobExperienceRequest request = new GetAllJobExperienceRequest(UUID.randomUUID());

        // Act
        List<GetJobExperienceResponse> actual = sut.getAllJobExperience(request);
        List<GetJobExperienceResponse> expected = List.of(
            new GetJobExperienceResponse("jobName1", 1, 1, 50)
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void getAllJobExperience_WithEmptyJob_ShouldThrowNoSuchElementException() {
        // Arrange
        String jobId = UUID.randomUUID().toString();
        List<JobExperience> jobExperienceList = List.of(
            new JobExperience("id1", "accountId1", jobId, 1)
        );
        when(jobDataMock.getAllJobExperience(any(GetAllJobExperienceRequest.class)))
            .thenReturn(jobExperienceList);
        when(jobDataMock.getJob(any(GetJobRequest.class))).thenReturn(Optional.empty());

        GetAllJobExperienceRequest request = new GetAllJobExperienceRequest(UUID.randomUUID());

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.getAllJobExperience(request)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_ShouldCallDataLayer() {
        // Arrange
        when(jobDataMock.updateJobExperience(any(AddExperienceRequest.class))).thenReturn(1);

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            40
        );

        // Act
        sut.addExperience(request);

        // Assert
        verify(jobDataMock, times(1)).updateJobExperience(request);
    }

    @Test
    @Tag("Unit")
    public void createJobExperience_ShouldCallDataLayer() {
        // Arrange
        when(jobDataMock.createJobExperience(any(CreateJobExperienceRequest.class))).thenReturn(1);

        CreateJobExperienceRequest request = new CreateJobExperienceRequest(UUID.randomUUID());

        // Act
        sut.createJobExperience(request);

        // Assert
        verify(jobDataMock, times(1)).createJobExperience(request);
    }

    @Test
    @Tag("Unit")
    public void addPlayerExperienceBar_getPlayerExperienceBar_ShouldAddExperienceBarToMap() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        ExperienceBar experienceBar = new ExperienceBar(null, null);

        // Act
        sut.addPlayerExperienceBar(uuid, experienceBar);

        // Assert
        assertNotNull(sut.getPlayerExperienceBar(uuid));
    }
}
