package com.erigitic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.erigitic.TestUtils;
import com.erigitic.data.AccountData;
import com.erigitic.data.CurrencyData;
import com.erigitic.data.Database;
import com.erigitic.domain.Balance;
import com.erigitic.domain.TEAccount;
import com.erigitic.domain.TECurrency;
import com.erigitic.services.AccountService;
import com.erigitic.services.TEEconomyService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import net.kyori.adventure.key.Key;
import org.h2.value.Value;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.block.entity.CommandBlock;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@ExtendWith(MockitoExtension.class)
public class PayCommandTest {
    private PayCommand sut;

    @Mock
    private Database databaseMock;

    @Mock
    private TEEconomyService economyServiceMock;

    @Mock
    private AccountService accountServiceMock;

    @Mock
    private Player playerMock;

    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerCommandSource_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(CommandBlock.class);
        sut = new PayCommand(economyServiceMock, accountServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Only players can use this command";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithMissingAmountArgument_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(mock(Player.class));
        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<BigDecimal>>any())).thenReturn(Optional.empty());

        sut = new PayCommand(economyServiceMock, accountServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Amount argument is missing";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithAmountOfZero_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(mock(Player.class));
        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<BigDecimal>>any())).thenReturn(Optional.of(BigDecimal.ZERO));

        sut = new PayCommand(economyServiceMock, accountServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Amount must be greater than 0";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithAmountLessThanZero_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(mock(Player.class));
        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<BigDecimal>>any())).thenReturn(Optional.of(BigDecimal.valueOf(-1)));

        sut = new PayCommand(economyServiceMock, accountServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Amount must be greater than 0";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithUnsuccessfulTransfer_ShouldThrowCommandException() {
        Player toPlayerMock = mock(Player.class);
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(playerMock);

        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<?>>any())).thenAnswer(invocationOnMock -> {
            TECommandParameterKey<?> commandParameterKey = invocationOnMock.getArgument(0);
            String argument = commandParameterKey.key();

            if (argument.equals("amount")) {
                return Optional.of(BigDecimal.TEN);
            } else if (argument.equals("player")) {
                return Optional.of(toPlayerMock);
            }

            return Optional.empty();
        });

        sut = new PayCommand(economyServiceMock, accountServiceMock);

        when(playerMock.uniqueId()).thenReturn(UUID.randomUUID());
        when(toPlayerMock.uniqueId()).thenReturn(UUID.randomUUID());
        when(economyServiceMock.findOrCreateAccount(any(UUID.class)))
            .thenReturn(Optional.of(mock(UniqueAccount.class)))
            .thenReturn(Optional.of(mock(UniqueAccount.class)));
        when(economyServiceMock.defaultCurrency()).thenReturn(mock(TECurrency.class));
        when(accountServiceMock.setTransferBalances(any(Balance.class), any(Balance.class))).thenReturn(false);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Failed to run command: unable to set balances";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithMissingPlayerArgument_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(playerMock);
        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<?>>any())).thenAnswer(invocationOnMock -> {
            TECommandParameterKey<?> commandParameterKey = invocationOnMock.getArgument(0);
            String argument = commandParameterKey.key();

            if (argument.equals("amount")) {
                return Optional.of(BigDecimal.TEN);
            } else if (argument.equals("player")) {
                return Optional.empty();
            }

            return Optional.empty();
        });

        sut = new PayCommand(economyServiceMock, accountServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Player argument is missing";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithPlayerArgumentTheSameAsExecutor_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(playerMock);
        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<?>>any())).thenAnswer(invocationOnMock -> {
            TECommandParameterKey<?> commandParameterKey = invocationOnMock.getArgument(0);
            String argument = commandParameterKey.key();

            if (argument.equals("amount")) {
                return Optional.of(BigDecimal.valueOf(100));
            } else if (argument.equals("player")) {
                return Optional.of(playerMock);
            }

            return Optional.empty();
        });

        sut = new PayCommand(economyServiceMock, accountServiceMock);

        when(playerMock.uniqueId()).thenReturn(UUID.randomUUID());
        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "You cannot pay yourself";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithGetOrCreateAccountIssue_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(playerMock);
        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<?>>any())).thenAnswer(invocationOnMock -> {
            TECommandParameterKey<?> commandParameterKey = invocationOnMock.getArgument(0);
            String argument = commandParameterKey.key();

            if (argument.equals("amount")) {
                return Optional.of(BigDecimal.valueOf(100));
            } else if (argument.equals("player")) {
                return Optional.of(mock(Player.class));
            }

            return Optional.empty();
        });

        sut = new PayCommand(economyServiceMock, accountServiceMock);

        when(playerMock.uniqueId()).thenReturn(UUID.randomUUID());
        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Failed to run command: invalid account(s)";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Integration")
    public void execute_WithValidData_ShouldUpdateBalances() throws SQLException, CommandException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedUsers();
        when(databaseMock.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection());

        AccountData accountData = new AccountData(databaseMock);
        CurrencyData currencyData = new CurrencyData(databaseMock);
        EconomyService economyService = new TEEconomyService(accountData, currencyData);
        AccountService accountService = new AccountService(accountData);
        Player toPlayer = mock(Player.class);
        sut = new PayCommand(economyService, accountService);

        when(toPlayer.uniqueId()).thenReturn(UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea"));

        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(playerMock);

        when(ctx.one(ArgumentMatchers.<TECommandParameterKey<?>>any())).thenAnswer(invocationOnMock -> {
            TECommandParameterKey<?> commandParameterKey = invocationOnMock.getArgument(0);
            String argument = commandParameterKey.key();

            if (argument.equals("amount")) {
                return Optional.of(BigDecimal.TEN);
            } else if (argument.equals("player")) {
                return Optional.of(toPlayer);
            }

            return Optional.empty();
        });
        when(playerMock.uniqueId()).thenReturn(UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"));

        // Act
        CommandResult result = sut.execute(ctx);

        // Assert
        assertTrue(result.isSuccess());

        try (Connection conn = TestUtils.getConnection()) {
            assert conn != null;

            Statement fromPlayerBalanceStmt = conn.createStatement();
            ResultSet fromPlayerBalanceResult = fromPlayerBalanceStmt.executeQuery(
                "SELECT balance FROM te_balance\n"
                + "WHERE currency_id=1 AND user_id='62694fb0-07cc-4396-8d63-4f70646d75f0'"
            );
            fromPlayerBalanceResult.next();

            BigDecimal fromPlayerBalance = fromPlayerBalanceResult.getBigDecimal("balance");
            BigDecimal expectedFromPlayerBalance = BigDecimal.valueOf(40);
            assertEquals(expectedFromPlayerBalance, fromPlayerBalance);

            Statement toPlayerBalanceStmt = conn.createStatement();
            ResultSet toPlayerBalanceResult = toPlayerBalanceStmt.executeQuery(
                "SELECT balance FROM te_balance\n"
                    + "WHERE currency_id=1 AND user_id='551fe9be-f77f-4bcb-81db-548db6e77aea'"
            );
            toPlayerBalanceResult.next();

            BigDecimal toPlayerBalance = toPlayerBalanceResult.getBigDecimal("balance");
            BigDecimal expectedToPlayerBalance = BigDecimal.valueOf(110);
            assertEquals(expectedToPlayerBalance, toPlayerBalance);
        }
    }
}
