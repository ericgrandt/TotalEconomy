package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.jobs.data.ActiveJobData;
import com.ericgrandt.totaleconomy.jobs.exception.ActiveJobNotFoundException;
import com.ericgrandt.totaleconomy.jobs.model.ActiveJob;
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
    private ActiveJobData jobDataMock;

    private JobService sut;

    @BeforeEach
    public void setUp() throws SQLException {
        lenient().when(transactionUtilMock.runInTransaction(any())).thenAnswer(invocation -> {
            TransactionUtil.Transaction<?> tx = invocation.getArgument(0);
            return tx.execute(mock(Connection.class));
        });
        sut = new JobService(transactionUtilMock, jobDataMock);
    }

    @Test
    @Tag("Unit")
    public void getActiveJob_WithAnActiveJob_ShouldReturnActiveJob() throws SQLException {
        // Arrange
        var playerId = UUID.randomUUID();
        var activeJob = new ActiveJob(playerId, "miner");
        when(jobDataMock.getActiveJob(any(), eq(playerId))).thenReturn(Optional.of(activeJob));

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
        when(jobDataMock.getActiveJob(any(), eq(playerId))).thenReturn(Optional.empty());

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
        when(jobDataMock.getActiveJob(any(), eq(playerId))).thenThrow(SQLException.class);

        // Act/Assert
        assertThrows(
            DatabaseException.class,
            () -> sut.getActiveJob(playerId)
        );
    }
}
