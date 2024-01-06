package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.models.TransferResult;
import com.ericgrandt.totaleconomy.models.TransferResult.ResultType;
import com.ericgrandt.totaleconomy.services.BalanceService;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PayCommandTest {
    @Mock
    private Logger loggerMock;

    private final CurrencyDto defaultCurrency = new CurrencyDto(
        1,
        "singular",
        "plural",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldSendMessageAndReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        CommandSender senderMock = mock(ConsoleCommandSender.class);
        PayCommand sut = new PayCommand(loggerMock, bukkitWrapperMock, economyMock, mock(BalanceService.class));

        // Act
        String[] args = {"", ""};
        boolean actual = sut.onCommand(senderMock, mock(Command.class), "pay", args);

        // Assert
        verify(senderMock).sendMessage("Only players can run this command");
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithLessThanTwoArgs_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);

        PayCommand sut = new PayCommand(loggerMock, bukkitWrapperMock, economyMock, mock(BalanceService.class));

        // Act
        String[] args = {"playerName"};
        boolean actual = sut.onCommand(
            mock(Player.class),
            mock(Command.class),
            "pay",
            args
        );

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithInvalidPlayerArg_ShouldSendMessage() {
        // Arrange
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(
            loggerMock,
            mock(BukkitWrapper.class),
            mock(EconomyImpl.class),
            mock(BalanceService.class)
        );

        // Act
        sut.onCommandHandler(playerMock, null, "100");

        // Assert
        verify(playerMock).sendMessage("Invalid player specified");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithInvalidAmountArg_ShouldSendMessage() {
        // Arrange
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(
            loggerMock,
            mock(BukkitWrapper.class),
            mock(EconomyImpl.class),
            mock(BalanceService.class)
        );

        // Act
        sut.onCommandHandler(playerMock, mock(Player.class), "not a double");

        // Assert
        verify(playerMock).sendMessage("Invalid amount specified");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithSameSenderAndTarget_ShouldSendMessage() {
        // Arrange
        Player playerMock = mock(Player.class);
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(
            loggerMock,
            mock(BukkitWrapper.class),
            mock(EconomyImpl.class),
            mock(BalanceService.class)
        );

        // Act
        sut.onCommandHandler(playerMock, playerMock, "100");

        // Assert
        verify(playerMock).sendMessage("You cannot pay yourself");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithAmountContainingTooManyDecimalPlaces_ShouldCallTransferWithScaledAmount() throws SQLException {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        BalanceService balanceServiceMock = mock(BalanceService.class);
        Player playerMock = mock(Player.class);
        UUID playerUUID = UUID.randomUUID();
        Player targetMock = mock(Player.class);
        UUID targetUUID = UUID.randomUUID();
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(balanceServiceMock.transfer(any(UUID.class), any(UUID.class), anyDouble())).thenReturn(
            new TransferResult(ResultType.SUCCESS, "")
        );
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(targetMock.getUniqueId()).thenReturn(targetUUID);

        PayCommand sut = new PayCommand(loggerMock, mock(BukkitWrapper.class), economyMock, balanceServiceMock);

        // Act
        sut.onCommandHandler(
            playerMock,
            targetMock,
            "100.0191"
        );

        // Assert
        verify(balanceServiceMock).transfer(playerUUID, targetUUID, 100.01);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithAmountContainingTooLittleDecimalPlaces_ShouldCallTransferWithScaledAmount() throws SQLException {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        BalanceService balanceServiceMock = mock(BalanceService.class);
        Player playerMock = mock(Player.class);
        UUID playerUUID = UUID.randomUUID();
        Player targetMock = mock(Player.class);
        UUID targetUUID = UUID.randomUUID();
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(balanceServiceMock.transfer(any(UUID.class), any(UUID.class), anyDouble())).thenReturn(
            new TransferResult(ResultType.SUCCESS, "")
        );
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(targetMock.getUniqueId()).thenReturn(targetUUID);

        PayCommand sut = new PayCommand(loggerMock, mock(BukkitWrapper.class), economyMock, balanceServiceMock);

        // Act
        sut.onCommandHandler(
            playerMock,
            targetMock,
            "100.1"
        );

        // Assert
        verify(balanceServiceMock).transfer(playerUUID, targetUUID, 100.10);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithFailedTransfer_ShouldSendMessage() throws SQLException {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        BalanceService balanceServiceMock = mock(BalanceService.class);
        Player playerMock = mock(Player.class);
        UUID playerUUID = UUID.randomUUID();
        Player targetMock = mock(Player.class);
        UUID targetUUID = UUID.randomUUID();
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(balanceServiceMock.transfer(any(UUID.class), any(UUID.class), anyDouble())).thenReturn(
            new TransferResult(ResultType.FAILURE, "Test error message")
        );
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(targetMock.getUniqueId()).thenReturn(targetUUID);

        PayCommand sut = new PayCommand(loggerMock, mock(BukkitWrapper.class), economyMock, balanceServiceMock);

        // Act
        sut.onCommandHandler(
            playerMock,
            targetMock,
            "100.10"
        );

        // Assert
        verify(playerMock).sendMessage("Test error message");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithSqlExceptionFromTransfer_ShouldLogErrorAndSendMessage() throws SQLException {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        BalanceService balanceServiceMock = mock(BalanceService.class);
        Player playerMock = mock(Player.class);
        UUID playerUUID = UUID.randomUUID();
        Player targetMock = mock(Player.class);
        UUID targetUUID = UUID.randomUUID();
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(balanceServiceMock.transfer(any(UUID.class), any(UUID.class), anyDouble())).thenThrow(SQLException.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(targetMock.getUniqueId()).thenReturn(targetUUID);

        PayCommand sut = new PayCommand(loggerMock, mock(BukkitWrapper.class), economyMock, balanceServiceMock);

        // Act
        sut.onCommandHandler(
            playerMock,
            targetMock,
            "100.10"
        );

        // Assert
        verify(loggerMock).log(
            eq(Level.SEVERE),
            eq("An exception occurred during the handling of the pay command."),
            any(SQLException.class)
        );
        verify(playerMock).sendMessage("Error executing command. Contact an administrator.");
    }

    @Test
    @Tag("Integration")
    public void onCommandHandler_ShouldTransferMoney() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        UUID playerUUID = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID targetUUID = UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea");

        Player playerMock = mock(Player.class);
        String playerName = "Player 1";
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(playerMock.getName()).thenReturn(playerName);

        Player targetMock = mock(Player.class);
        String targetName = "Player 2";
        when(targetMock.getUniqueId()).thenReturn(targetUUID);
        when(targetMock.getName()).thenReturn(targetName);

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());

        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "Dollar",
            "Dollars",
            "$",
            2,
            true
        );
        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);

        PayCommand sut = new PayCommand(
            loggerMock,
            mock(BukkitWrapper.class),
            new EconomyImpl(loggerMock, true, defaultCurrency, accountData, balanceData),
            new BalanceService(balanceData)
        );

        // Act
        sut.onCommandHandler(playerMock, targetMock, String.valueOf(25));

        BigDecimal actualPlayerBalance = balanceData.getBalance(playerUUID, 1);
        BigDecimal expectedPlayerBalance = BigDecimal.valueOf(25.00).setScale(2, RoundingMode.DOWN);
        BigDecimal actualTargetBalance = balanceData.getBalance(targetUUID, 1);
        BigDecimal expectedTargetBalance = BigDecimal.valueOf(125.00).setScale(2, RoundingMode.DOWN);

        // Assert
        assertEquals(expectedPlayerBalance, actualPlayerBalance);
        assertEquals(expectedTargetBalance, actualTargetBalance);

        verify(playerMock, times(1)).sendMessage(
            String.format("You sent $25.00 to %s", targetName)
        );
        verify(targetMock, times(1)).sendMessage(
            String.format("You received $25.00 from %s", playerName)
        );
    }
}
