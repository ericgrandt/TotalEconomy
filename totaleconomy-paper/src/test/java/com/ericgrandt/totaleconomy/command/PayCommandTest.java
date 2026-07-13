package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.TEEconomyService;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.util.TestTaskRunner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PayCommandTest {
    @Mock
    private Plugin pluginMock;

    @Mock
    private Logger loggerMock;

    @Mock
    private Player playerMock;

    @Mock
    private Player toPlayerMock;

    @Mock
    private PlayerSelectorArgumentResolver resolverMock;

    private final AsyncTaskRunner taskRunner = new TestTaskRunner();
    private final CurrencyData currencyData = new CurrencyData();
    private final AccountData accountData = new AccountData();

    @Test
    @Tag("Integration")
    @SuppressWarnings("unchecked")
    public void onCommand_WithCurrencyCodeArgument_ShouldTransferCurrency() throws SQLException, CommandSyntaxException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var currency = TestUtils.seedCurrency(dataSource);
        var fromAccount = TestUtils.seedAccount(dataSource, currency.code());
        var toAccount = TestUtils.seedAccount(dataSource, currency.code());

        when(playerMock.getUniqueId()).thenReturn(UUID.fromString(fromAccount.playerId()));
        when(playerMock.getName()).thenReturn("FromPlayer");
        when(toPlayerMock.getUniqueId()).thenReturn(UUID.fromString(toAccount.playerId()));
        when(toPlayerMock.getName()).thenReturn("ToPlayer");

        var transactionUtil = new TransactionUtil(dataSource);
        var exceptionMapper = new CommandExceptionMapper(loggerMock);
        var economyService = new TEEconomyService(transactionUtil, currencyData, accountData);

        var ctx = mock(CommandContext.class);
        var source = mock(CommandSourceStack.class);
        when(ctx.getSource()).thenReturn(source);
        when(ctx.getArgument("toPlayer", PlayerSelectorArgumentResolver.class)).thenReturn(resolverMock);
        when(resolverMock.resolve(any())).thenReturn(List.of(toPlayerMock));
        when(ctx.getArgument("amount", Double.class)).thenReturn(1.0);
        when(ctx.getArgument("currency", String.class)).thenReturn("COIN");
        when(source.getSender()).thenReturn(playerMock);

        var sut = new PayCommand(pluginMock, taskRunner, exceptionMapper, economyService);

        // Act
        var actual = sut.executeWithCurrency(ctx);
        var expected = Command.SINGLE_SUCCESS;

        // Assert
        assertEquals(expected, actual);
        verify(playerMock).sendMessage(Messages.payFrom(Component.text("1 Coin"), toPlayerMock.getName()));
        verify(toPlayerMock).sendMessage(Messages.payTo(Component.text("1 Coin"), playerMock.getName()));

        var finalFromBalance = economyService.getAccountBalance(UUID.fromString(fromAccount.playerId()), "COIN");
        var finalToBalance = economyService.getAccountBalance(UUID.fromString(toAccount.playerId()), "COIN");

        assertEquals(
            BigDecimal.valueOf(9).setScale(0, RoundingMode.DOWN),
            finalFromBalance.balance().setScale(0, RoundingMode.DOWN)
        );
        assertEquals(
            BigDecimal.valueOf(11).setScale(0, RoundingMode.DOWN),
            finalToBalance.balance().setScale(0, RoundingMode.DOWN)
        );
    }

    @Test
    @Tag("Integration")
    @SuppressWarnings("unchecked")
    public void onCommand_WithNoCurrencyCodeArgument_ShouldTransferDefaultCurrency() throws SQLException, CommandSyntaxException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        TestUtils.seedDefaultCurrency(dataSource);
        var fromAccount = TestUtils.seedAccount(dataSource, null);
        var toAccount = TestUtils.seedAccount(dataSource, null);

        when(playerMock.getUniqueId()).thenReturn(UUID.fromString(fromAccount.playerId()));
        when(playerMock.getName()).thenReturn("FromPlayer");
        when(toPlayerMock.getUniqueId()).thenReturn(UUID.fromString(toAccount.playerId()));
        when(toPlayerMock.getName()).thenReturn("ToPlayer");

        var transactionUtil = new TransactionUtil(dataSource);
        var exceptionMapper = new CommandExceptionMapper(loggerMock);
        var economyService = new TEEconomyService(transactionUtil, currencyData, accountData);

        var ctx = mock(CommandContext.class);
        var source = mock(CommandSourceStack.class);
        when(ctx.getSource()).thenReturn(source);
        when(ctx.getArgument("toPlayer", PlayerSelectorArgumentResolver.class)).thenReturn(resolverMock);
        when(resolverMock.resolve(any())).thenReturn(List.of(toPlayerMock));
        when(ctx.getArgument("amount", Double.class)).thenReturn(1.0);
        when(source.getSender()).thenReturn(playerMock);

        var sut = new PayCommand(pluginMock, taskRunner, exceptionMapper, economyService);

        // Act
        var actual = sut.executeWithDefault(ctx);
        var expected = Command.SINGLE_SUCCESS;

        // Assert
        assertEquals(expected, actual);
        verify(playerMock).sendMessage(Messages.payFrom(Component.text("$1.00"), toPlayerMock.getName()));
        verify(toPlayerMock).sendMessage(Messages.payTo(Component.text("$1.00"), playerMock.getName()));

        var finalFromBalance = economyService.getAccountBalance(UUID.fromString(fromAccount.playerId()));
        var finalToBalance = economyService.getAccountBalance(UUID.fromString(toAccount.playerId()));

        assertEquals(
            BigDecimal.valueOf(9).setScale(2, RoundingMode.DOWN),
            finalFromBalance.balance().setScale(2, RoundingMode.DOWN)
        );
        assertEquals(
            BigDecimal.valueOf(11).setScale(2, RoundingMode.DOWN),
            finalToBalance.balance().setScale(2, RoundingMode.DOWN)
        );
    }
}
