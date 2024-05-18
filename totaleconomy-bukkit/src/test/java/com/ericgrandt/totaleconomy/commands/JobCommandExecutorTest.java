package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.command.JobCommand;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import com.ericgrandt.totaleconomy.common.services.JobService;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobCommandExecutorTest {
    @Mock
    private Player playerMock;

    @Mock
    private Database databaseMock;

    @Mock
    private CommonLogger loggerMock;

    @Test
    @Tag("Integration")
    public void onCommand_ShouldSendMessageWithJobLevelsToPlayer() throws SQLException {
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
        JobCommand jobCommand = new JobCommand(jobService);
        JobCommandExecutor sut = new JobCommandExecutor(jobCommand);

        // Act
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "", new String[0]);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));
        verify(playerMock, times(1)).sendMessage(any(Component.class));
        assertTrue(actual);
    }
}
