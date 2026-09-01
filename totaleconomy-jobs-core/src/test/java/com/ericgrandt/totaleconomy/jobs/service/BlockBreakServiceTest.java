package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.api.service.EconomyService;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import com.ericgrandt.totaleconomy.jobs.data.JobExperienceData;
import com.ericgrandt.totaleconomy.jobs.dto.HandleActionDto;
import com.ericgrandt.totaleconomy.jobs.dto.Status;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.job.JobCalculator;
import com.ericgrandt.totaleconomy.jobs.model.JobExperience;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlockBreakServiceTest {
    @Mock
    private TransactionUtil transactionUtilMock;

    @Mock
    private JobExperienceData jobExperienceDataMock;

    @Mock
    private EconomyService<?> economyServiceMock;

    @Mock
    private JobCalculator jobCalculatorMock;

    private final Config.Job.Action.Entry entry = new Config.Job.Action.Entry("material", 1, BigDecimal.ONE);

    private BlockBreakService sut;

    @BeforeEach
    public void setUp() throws SQLException {
        lenient().when(transactionUtilMock.runInTransaction(any())).thenAnswer(invocation -> {
            TransactionUtil.Transaction<?> tx = invocation.getArgument(0);
            return tx.execute(mock(Connection.class));
        });
        sut = new BlockBreakService(transactionUtilMock, jobExperienceDataMock, economyServiceMock, jobCalculatorMock);
    }

    @Test
    @Tag("Unit")
    public void handleAction_WithSuccess_ShouldReturnCorrectHandleActionDto() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobName = "testJob";
        var blockName = "testBlock";

        when(jobCalculatorMock.getEntry(
            jobName,
            JobEnums.ActionType.BLOCK_BREAK,
            blockName
        )).thenReturn(Optional.of(entry));
        when(jobExperienceDataMock.getJobExperience(any(), any())).thenReturn(Optional.of(new JobExperience(
            playerId,
            jobName,
            10
        )));
        when(jobExperienceDataMock.upsertJobExperience(
            any(),
            eq(new UpsertJobExperienceDto(playerId, jobName, 1))
        )).thenReturn(new JobExperience(playerId, jobName, 11));

        // Act
        var actual = sut.handleAction(playerId, jobName, blockName);
        var expected = new HandleActionDto(Status.SUCCESS, entry.xp(), entry.payout(), false);

        // Assert
        assertEquals(expected, actual);
        verify(jobCalculatorMock).calculateLevelFromExp(10);
        verify(jobCalculatorMock).calculateLevelFromExp(11);
    }

    @Test
    @Tag("Unit")
    public void handleAction_WithNoEntryForBlock_ShouldReturnNoEntryHandleActionDto() {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobName = "testJob";
        var blockName = "testBlock";

        when(jobCalculatorMock.getEntry(
            jobName,
            JobEnums.ActionType.BLOCK_BREAK,
            blockName
        )).thenReturn(Optional.empty());

        // Act
        var actual = sut.handleAction(playerId, jobName, blockName);
        var expected = new HandleActionDto(Status.NOENTRY, 0, BigDecimal.ZERO, false);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void handleAction_WithNoJobExperienceRow_ShouldUseDefaultJobExperience() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobName = "testJob";
        var blockName = "testBlock";

        when(jobCalculatorMock.getEntry(
            jobName,
            JobEnums.ActionType.BLOCK_BREAK,
            blockName
        )).thenReturn(Optional.of(entry));
        when(jobExperienceDataMock.getJobExperience(any(), any())).thenReturn(Optional.empty());
        when(jobExperienceDataMock.upsertJobExperience(
            any(),
            eq(new UpsertJobExperienceDto(playerId, jobName, 1))
        )).thenReturn(new JobExperience(playerId, jobName, 1));

        // Act
        var actual = sut.handleAction(playerId, jobName, blockName);
        var expected = new HandleActionDto(Status.SUCCESS, entry.xp(), entry.payout(), false);

        // Assert
        assertEquals(expected, actual);
        verify(jobCalculatorMock).calculateLevelFromExp(0);
        verify(jobCalculatorMock).calculateLevelFromExp(1);
    }

    @Test
    @Tag("Unit")
    public void handleAction_WithLevelUp_ShouldReturnHandleActionDto() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobName = "testJob";
        var blockName = "testBlock";

        when(jobCalculatorMock.getEntry(
            jobName,
            JobEnums.ActionType.BLOCK_BREAK,
            blockName
        )).thenReturn(Optional.of(entry));
        when(jobExperienceDataMock.getJobExperience(any(), any())).thenReturn(Optional.of(new JobExperience(
            playerId,
            jobName,
            10
        )));
        when(jobExperienceDataMock.upsertJobExperience(
            any(),
            eq(new UpsertJobExperienceDto(playerId, jobName, 1))
        )).thenReturn(new JobExperience(playerId, jobName, 11));
        when(jobCalculatorMock.calculateLevelFromExp(10)).thenReturn(1);
        when(jobCalculatorMock.calculateLevelFromExp(11)).thenReturn(2);

        // Act
        var actual = sut.handleAction(playerId, jobName, blockName);
        var expected = new HandleActionDto(Status.SUCCESS, entry.xp(), entry.payout(), true);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void handleAction_WithSQLException_ShouldThrowDatabaseException() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var jobName = "testJob";
        var blockName = "testBlock";

        when(jobCalculatorMock.getEntry(
            jobName,
            JobEnums.ActionType.BLOCK_BREAK,
            blockName
        )).thenReturn(Optional.of(entry));
        when(jobExperienceDataMock.getJobExperience(any(), any())).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(DatabaseException.class, () -> sut.handleAction(playerId, jobName, blockName));
    }
}
