package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.commonimpl.BukkitLogger;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandExecutorTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Integration")
    public void onCommand_ShouldSendMessageWithBalanceToPlayer() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        Player playerMock = mock(Player.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());
        when(playerMock.getUniqueId()).thenReturn(
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
        CurrencyData currencyData = new CurrencyData(databaseMock);
        CommonEconomy economy = new CommonEconomy(new BukkitLogger(loggerMock), accountData, balanceData, currencyData);

        BalanceCommandExecutor sut = new BalanceCommandExecutor(economy, defaultCurrency);

        // Act
        sut.onCommand(playerMock, mock(Command.class), "", new String[0]);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));
        verify(playerMock).sendMessage(Component.text("Balance: ").append(Component.text("$50.00")));
    }
}
