package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.models.JobExperience;
import com.ericgrandt.totaleconomy.services.JobService;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobCommandTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        JobCommand sut = new JobCommand(loggerMock, mock(JobService.class));

        // Act
        boolean actual = sut.onCommand(
            mock(ConsoleCommandSender.class),
            mock(Command.class),
            "",
            null
        );

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithSuccess_ShouldReturnTrue() {
        // Arrange
        JobCommand sut = new JobCommand(loggerMock, mock(JobService.class));

        // Act
        boolean actual = sut.onCommand(
            mock(Player.class),
            mock(Command.class),
            "",
            null
        );

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithSuccess_ShouldSendPlayerMessage() throws SQLException {
        // Arrange
        UUID playerUuid = UUID.randomUUID();
        List<JobExperience> jobExperienceList = List.of(
            new JobExperience("job1", 0, 0, 10, 1),
            new JobExperience("job2", 35, 0, 50, 3)
        );

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerUuid);
        when(jobServiceMock.getExperienceForAllJobs(playerUuid)).thenReturn(jobExperienceList);

        JobCommand sut = new JobCommand(loggerMock, jobServiceMock);

        // Act
        sut.onCommandHandler(playerMock);
        Component expected = Component.newline()
            .append(Component.text("Jobs", TextColor.fromHexString("#708090"), TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("job1", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" [LVL", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text(" 1", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text("] [", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text("0/10", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" EXP]", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("job2", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" [LVL", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text(" 3", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text("] [", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text("35/50", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" EXP]", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.newline());

        // Assert
        verify(playerMock, times(1)).sendMessage(expected);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithException_ShouldSendPlayerErrorMessage() throws SQLException {
        // Arrange
        UUID playerUuid = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerUuid);
        when(jobServiceMock.getExperienceForAllJobs(playerUuid)).thenThrow(SQLException.class);

        JobCommand sut = new JobCommand(loggerMock, jobServiceMock);

        // Act
        sut.onCommandHandler(playerMock);

        // Assert
        verify(playerMock).sendMessage(
            Component.text("An error has occurred. Please contact an administrator.", NamedTextColor.RED)
        );
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithException_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUuid = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerUuid);
        when(jobServiceMock.getExperienceForAllJobs(playerUuid)).thenThrow(SQLException.class);

        JobCommand sut = new JobCommand(loggerMock, jobServiceMock);

        // Act
        sut.onCommandHandler(playerMock);

        // Assert
        verify(loggerMock).log(
            eq(Level.SEVERE),
            eq("An exception occurred during the handling of the job command."),
            any(SQLException.class)
        );
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

        Database databaseMock = mock(Database.class);
        Player playerMock = mock(Player.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());
        when((playerMock).getUniqueId()).thenReturn(playerUuid);

        JobData jobData = new JobData(databaseMock);
        JobService jobService = new JobService(loggerMock, jobData);
        JobCommand sut = new JobCommand(loggerMock, jobService);

        // Act
        sut.onCommandHandler(playerMock);
        Component expected = Component.newline()
            .append(Component.text("Jobs", TextColor.fromHexString("#708090"), TextDecoration.BOLD, TextDecoration.UNDERLINED))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Test Job 1", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" [LVL", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text(" 2", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text("] [", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text("50/197", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" EXP]", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("Test Job 2", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" [LVL", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text(" 1", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text("] [", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.text("10/50", TextColor.fromHexString("#DADFE1"), TextDecoration.BOLD))
            .append(Component.text(" EXP]", TextColor.fromHexString("#708090"), TextDecoration.BOLD))
            .append(Component.newline());

        // Assert
        verify(playerMock).sendMessage(expected);
    }
}
