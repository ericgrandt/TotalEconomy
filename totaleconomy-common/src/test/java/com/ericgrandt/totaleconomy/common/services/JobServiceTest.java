package com.ericgrandt.totaleconomy.common.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.domain.Job;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.AddExperienceResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private JobData jobDataMock;

    @Test
    @Tag("Unit")
    public void addExperience_WithSuccessAndLevelUp_ShouldReturnAddExperienceResponse() {
        // Arrange
        JobReward jobReward = new JobReward("", "", "", 1, "", BigDecimal.ONE, 30);
        Job job = new Job("", "job");
        JobExperience jobExperience = new JobExperience("", "", "", 20);
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.of(jobReward));
        when(jobDataMock.getJob(any(UUID.class))).thenReturn(Optional.of(job));
        when(jobDataMock.getJobExperience(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(jobExperience));
        when(jobDataMock.updateJobExperience(any(JobExperience.class))).thenReturn(1);

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "",
            ""
        );
        JobService sut = new JobService(jobDataMock);

        // Act
        AddExperienceResponse actual = sut.addExperience(request);
        AddExperienceResponse expected = new AddExperienceResponse("job", true);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithSuccessAndNoLevelUp_ShouldReturnAddExperienceResponse() {
        // Arrange
        JobReward jobReward = new JobReward("", "", "", 1, "", BigDecimal.ONE, 10);
        Job job = new Job("", "miner");
        JobExperience jobExperience = new JobExperience("", "", "", 20);
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.of(jobReward));
        when(jobDataMock.getJob(any(UUID.class))).thenReturn(Optional.of(job));
        when(jobDataMock.getJobExperience(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(jobExperience));

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "",
            ""
        );
        JobService sut = new JobService(jobDataMock);

        // Act
        AddExperienceResponse actual = sut.addExperience(request);
        AddExperienceResponse expected = new AddExperienceResponse("miner", false);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithLevelUpButNoUpdatedRows_ShouldReturnWithNoLevelUp() {
        // Arrange
        JobReward jobReward = new JobReward("", "", "", 1, "", BigDecimal.ONE, 20);
        Job job = new Job("", "miner");
        JobExperience jobExperience = new JobExperience("", "", "", 40);
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.of(jobReward));
        when(jobDataMock.getJob(any(UUID.class))).thenReturn(Optional.of(job));
        when(jobDataMock.getJobExperience(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(jobExperience));
        when(jobDataMock.updateJobExperience(any(JobExperience.class))).thenReturn(0);

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "",
            ""
        );
        JobService sut = new JobService(jobDataMock);

        // Act
        AddExperienceResponse actual = sut.addExperience(request);
        AddExperienceResponse expected = new AddExperienceResponse("miner", false);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithEmptyJobReward_ShouldThrowNoSuchElementException() {
        // Arrange
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.empty());

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "",
            ""
        );
        JobService sut = new JobService(jobDataMock);

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.addExperience(request)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithEmptyJob_ShouldThrowNoSuchElementException() {
        // Arrange
        JobReward jobReward = new JobReward("", "", "", 1, "", BigDecimal.ONE, 10);
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.of(jobReward));
        when(jobDataMock.getJob(any(UUID.class))).thenReturn(Optional.empty());

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "",
            ""
        );
        JobService sut = new JobService(jobDataMock);

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.addExperience(request)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithEmptyJobExperience_ShouldThrowNoSuchElementException() {
        // Arrange
        JobReward jobReward = new JobReward("", "", "", 1, "", BigDecimal.ONE, 10);
        Job job = new Job("", "");
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.of(jobReward));
        when(jobDataMock.getJob(any(UUID.class))).thenReturn(Optional.of(job));
        when(jobDataMock.getJobExperience(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "",
            ""
        );
        JobService sut = new JobService(jobDataMock);

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.addExperience(request)
        );
    }
}
