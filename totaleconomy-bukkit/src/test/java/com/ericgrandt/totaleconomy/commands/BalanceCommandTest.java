package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        BalanceCommand sut = new BalanceCommand(economyMock);

        // Act
        boolean actual = sut.onCommand(mock(ConsoleCommandSender.class), mock(Command.class), "", null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_ShouldSendPlayerTheirBalance() {
        // Arrange
        BigDecimal balance = BigDecimal.valueOf(100).setScale(2, RoundingMode.DOWN);

        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        when(economyMock.getBalance(playerMock)).thenReturn(balance.doubleValue());
        when(economyMock.format(any(Double.class))).thenReturn("$" + balance);

        BalanceCommand sut = new BalanceCommand(economyMock);

        // Act
        sut.onCommandHandler(playerMock);

        // Assert
        verify(playerMock).sendMessage("Balance: $100.00");
    }

    @Test
    @Tag("Integration")
    public void onCommandHandler_ShouldSendMessageWithBalanceToPlayer() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        Player playerMock = mock(Player.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());
        when((playerMock).getUniqueId()).thenReturn(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );

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
        EconomyImpl economy = new EconomyImpl(loggerMock, true, defaultCurrency, accountData, balanceData);

        BalanceCommand sut = new BalanceCommand(economy);

        // Act
        sut.onCommandHandler(playerMock);

        // Assert
        verify(playerMock).sendMessage("Balance: $50.00");
    }
}
