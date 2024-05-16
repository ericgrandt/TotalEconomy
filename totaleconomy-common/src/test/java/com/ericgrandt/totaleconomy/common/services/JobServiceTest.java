package com.ericgrandt.totaleconomy.common.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import java.math.BigDecimal;
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
    public void getJobReward_WithSuccess_ShouldReturnGetJobRewardResponse() {
        // Arrange
        JobReward jobReward = new JobReward("", "", "", 1, "", BigDecimal.ONE, 30);
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.of(jobReward));

        GetJobRewardRequest request = new GetJobRewardRequest("", "");
        JobService sut = new JobService(jobDataMock);

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
        when(jobDataMock.getJobReward(any(String.class), any(String.class)))
            .thenReturn(Optional.empty());

        GetJobRewardRequest request = new GetJobRewardRequest("", "");
        JobService sut = new JobService(jobDataMock);

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.getJobReward(request)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_ShouldCallDataLayer() {
        // Arrange
        when(jobDataMock.updateJobExperience(
            any(String.class),
            any(String.class),
            any(Integer.class)
        )).thenReturn(1);

        AddExperienceRequest request = new AddExperienceRequest(
            UUID.randomUUID(),
            UUID.randomUUID(),
            40
        );
        JobService sut = new JobService(jobDataMock);

        // Act
        sut.addExperience(request);

        // Assert
        verify(jobDataMock, times(1)).updateJobExperience(
            any(String.class),
            any(String.class),
            any(Integer.class)
        );
    }
}
