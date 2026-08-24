package com.ericgrandt.totaleconomy.paper.command;

import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.common.testutils.TestTaskRunner;
import com.ericgrandt.totaleconomy.common.testutils.TestUtils;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.DatabaseBootstrapper;
import com.ericgrandt.totaleconomy.paper.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.CacheService;
import com.ericgrandt.totaleconomy.service.TEEconomyService;
import com.ericgrandt.totaleconomy.testutils.TestSeeder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private final TestTaskRunner taskRunner = new TestTaskRunner();
    private final CurrencyData currencyData = new CurrencyData();
    private final AccountData accountData = new AccountData();

    @Test
    @Tag("Integration")
    @SuppressWarnings("unchecked")
    public void onCommand_WithNoCurrencyCodeArgument_ShouldSendBalanceForDefaultCurrency() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseBootstrapper::initSchema);
        TestSeeder.seedDefaultCurrency(dataSource);
        var account = TestSeeder.seedAccount(dataSource, null);

        when(playerMock.getUniqueId()).thenReturn(UUID.fromString(account.playerId()));

        var transactionUtil = new TransactionUtil(dataSource);
        var exceptionMapper = new CommandExceptionMapper(loggerMock);
        var cacheService = new CacheService(transactionUtil, currencyData);
        var economyService = new TEEconomyService(transactionUtil, cacheService, currencyData, accountData);

        var ctx = mock(CommandContext.class);
        var source = mock(CommandSourceStack.class);
        when(ctx.getSource()).thenReturn(source);
        when(source.getSender()).thenReturn(playerMock);

        var sut = new BalanceCommand(pluginMock, taskRunner, exceptionMapper, economyService);

        // Act
        var actual = sut.executeWithDefault(ctx);
        var expected = Command.SINGLE_SUCCESS;

        // Assert
        assertEquals(expected, actual);
        verify(playerMock).sendMessage(Messages.balance(Component.text("$10.00")));
    }

    @Test
    @Tag("Integration")
    @SuppressWarnings("unchecked")
    public void onCommand_WithCurrencyCodeArgument_ShouldSendBalanceForCurrency() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseBootstrapper::initSchema);
        var currency = TestSeeder.seedCurrency(dataSource);
        var account = TestSeeder.seedAccount(dataSource, currency.code());

        when(playerMock.getUniqueId()).thenReturn(UUID.fromString(account.playerId()));

        var transactionUtil = new TransactionUtil(dataSource);
        var exceptionMapper = new CommandExceptionMapper(loggerMock);
        var cacheService = new CacheService(transactionUtil, currencyData);
        var economyService = new TEEconomyService(transactionUtil, cacheService, currencyData, accountData);

        var ctx = mock(CommandContext.class);
        var source = mock(CommandSourceStack.class);
        when(ctx.getSource()).thenReturn(source);
        when(ctx.getArgument("currency", String.class)).thenReturn("COIN");
        when(source.getSender()).thenReturn(playerMock);

        var sut = new BalanceCommand(pluginMock, taskRunner, exceptionMapper, economyService);

        // Act
        var actual = sut.executeWithCurrency(ctx);
        var expected = Command.SINGLE_SUCCESS;

        // Assert
        assertEquals(expected, actual);
        verify(playerMock).sendMessage(Messages.balance(Component.text("10 Coins")));
    }
}
