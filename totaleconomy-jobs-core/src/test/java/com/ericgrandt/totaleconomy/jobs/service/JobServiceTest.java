package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.jobs.data.ActiveJobData;
import com.ericgrandt.totaleconomy.jobs.data.JobExperienceData;
import com.ericgrandt.totaleconomy.jobs.dto.GetJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.exception.ActiveJobNotFoundException;
import com.ericgrandt.totaleconomy.jobs.exception.JobExperienceNotFoundException;
import com.ericgrandt.totaleconomy.jobs.model.ActiveJob;
import com.ericgrandt.totaleconomy.jobs.model.JobExperience;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private TransactionUtil transactionUtilMock;

    @Mock
    private ActiveJobData activeJobDataMock;

    @Mock
    private JobExperienceData jobExperienceDataMock;

    private JobService sut;

    @BeforeEach
    public void setUp() throws SQLException {
        lenient().when(transactionUtilMock.runInTransaction(any())).thenAnswer(invocation -> {
            TransactionUtil.Transaction<?> tx = invocation.getArgument(0);
            return tx.execute(mock(Connection.class));
        });
        sut = new JobService(transactionUtilMock, activeJobDataMock, jobExperienceDataMock);
    }

    @Test
    @Tag("Unit")
    public void getActiveJob_WithAnActiveJob_ShouldReturnActiveJob() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var activeJob = new ActiveJob(playerId, "miner");
        when(activeJobDataMock.getActiveJob(any(), eq(playerId))).thenReturn(Optional.of(activeJob));

        // Act
        var actual = sut.getActiveJob(playerId);

        // Assert
        assertEquals(activeJob, actual);
    }

    @Test
    @Tag("Unit")
    public void getActiveJob_WithNoActiveJob_ShouldThrowActiveJobNotFoundException() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        when(activeJobDataMock.getActiveJob(any(), eq(playerId))).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            ActiveJobNotFoundException.class,
            () -> sut.getActiveJob(playerId)
        );
    }

    @Test
    @Tag("Unit")
    public void getActiveJob_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        when(activeJobDataMock.getActiveJob(any(), eq(playerId))).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.getActiveJob(playerId)
        );
    }

    @Test
    @Tag("Unit")
    public void getJobExperience_WithJobExperience_ShouldReturnJobExperience() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobId = "miner";
        var jobExperience = new JobExperience(playerId, jobId, 100);
        var req = new GetJobExperienceDto(playerId, jobId);
        when(jobExperienceDataMock.getJobExperience(
            any(),
            eq(req)
        )).thenReturn(Optional.of(jobExperience));

        // Act
        var actual = sut.getJobExperience(req);

        // Assert
        assertEquals(jobExperience, actual);
    }

    @Test
    @Tag("Unit")
    public void getJobExperience_WithNoJobExperience_ShouldThrowJobExperienceNotFoundException() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobId = "miner";
        var req = new GetJobExperienceDto(playerId, jobId);
        when(jobExperienceDataMock.getJobExperience(any(), eq(req))).thenReturn(Optional.empty());

        // Act/Assert
        assertThrows(
            JobExperienceNotFoundException.class,
            () -> sut.getJobExperience(req)
        );
    }

    @Test
    @Tag("Unit")
    public void getJobExperience_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobId = "miner";
        var req = new GetJobExperienceDto(playerId, jobId);
        when(jobExperienceDataMock.getJobExperience(any(), eq(req))).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.getJobExperience(req)
        );
    }
}
