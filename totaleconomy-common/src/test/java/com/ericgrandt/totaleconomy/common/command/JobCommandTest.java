package com.ericgrandt.totaleconomy.common.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import com.ericgrandt.totaleconomy.common.models.GetAllJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceResponse;
import com.ericgrandt.totaleconomy.common.services.JobService;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class JobCommandTest {
    @Mock
    private JobService jobServiceMock;

    @Mock
    private CommonPlayer playerMock;

    @Mock
    private Database databaseMock;

    @Mock
    private CommonLogger loggerMock;

    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        JobCommand sut = new JobCommand(jobServiceMock);

        // Act
        boolean actual = sut.execute(mock(CommonSender.class), null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithSuccess_ShouldSendMessageWithJobExperience() {
        // Arrange
        List<GetJobExperienceResponse> jobExperienceList = List.of(
            new GetJobExperienceResponse("job1", 1, 10, 50),
            new GetJobExperienceResponse("job2", 1, 35, 50)
        );
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(jobServiceMock.getAllJobExperience(any(GetAllJobExperienceRequest.class))).thenReturn(
            jobExperienceList
        );

        JobCommand sut = new JobCommand(jobServiceMock);

        // Act
        boolean actual = sut.execute(playerMock, null);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));
        verify(playerMock, times(1)).sendMessage(any(Component.class));
        assertTrue(actual);
    }

    @Test
    @Tag("Integration")
    public void onCommandHandler_ShouldSendMessageWithJobLevelsToPlayer() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        UUID playerUuid = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());
        when(playerMock.getUniqueId()).thenReturn(playerUuid);

        JobData jobData = new JobData(loggerMock, databaseMock);
        JobService jobService = new JobService(jobData);
        JobCommand sut = new JobCommand(jobService);

        // Act
        boolean actual = sut.execute(playerMock, null);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));
        verify(playerMock, times(1)).sendMessage(any(Component.class));
        assertTrue(actual);
    }
}
