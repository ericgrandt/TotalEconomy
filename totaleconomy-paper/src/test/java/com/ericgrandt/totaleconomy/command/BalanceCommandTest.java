package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.EconomyService;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.util.TestTaskRunner;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private Plugin pluginMock;

    @Mock
    private Logger loggerMock;

    @Mock
    private Player playerMock;

    private final AsyncTaskRunner taskRunner = new TestTaskRunner();
    private final CurrencyData currencyData = new CurrencyData();
    private final AccountData accountData = new AccountData();

    @Test
    @Tag("Integration")
    public void onCommand_WithNoCurrencyCodeArgument_ShouldSendBalanceForDefaultCurrency() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var account = TestUtils.seedAccount(dataSource, null);

        when(playerMock.getUniqueId()).thenReturn(UUID.fromString(account.playerId()));

        var transactionUtil = new TransactionUtil(dataSource);
        var exceptionMapper = new CommandExceptionMapper(loggerMock);
        var economyService = new EconomyService(transactionUtil, currencyData, accountData);

        var sut = new BalanceCommand(pluginMock, taskRunner, exceptionMapper, economyService);

        // Act
        var actual = sut.onCommand(playerMock, mock(Command.class), "", new String[0]);

        // Assert
        assertTrue(actual);
        verify(playerMock).sendMessage(Messages.balance(Component.text("$10.00")));
    }

    @Test
    @Tag("Integration")
    public void onCommand_WithCurrencyCodeArgument_ShouldSendBalanceForCurrency() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedCurrency(dataSource);
        var account = TestUtils.seedAccount(dataSource, currency.code());

        when(playerMock.getUniqueId()).thenReturn(UUID.fromString(account.playerId()));

        var transactionUtil = new TransactionUtil(dataSource);
        var exceptionMapper = new CommandExceptionMapper(loggerMock);
        var economyService = new EconomyService(transactionUtil, currencyData, accountData);

        var sut = new BalanceCommand(pluginMock, taskRunner, exceptionMapper, economyService);

        // Act
        var actual = sut.onCommand(playerMock, mock(Command.class), "", new String[]{"COIN"});

        // Assert
        assertTrue(actual);
        verify(playerMock).sendMessage(Messages.balance(Component.text("10 Coins")));
    }
}
