package com.erigitic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.erigitic.TestUtils;
import com.erigitic.data.AccountData;
import com.erigitic.data.CurrencyData;
import com.erigitic.data.Database;
import com.erigitic.services.AccountService;
import com.erigitic.services.TEEconomyService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.block.tileentity.CommandBlock;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

@ExtendWith(MockitoExtension.class)
public class PayCommandTest {
    private PayCommand sut;

    @Mock
    private Database databaseMock;

    @Mock
    private TEEconomyService economyServiceMock;

    @Mock
    private Player playerMock;

    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerCommandSource_ShouldThrowCommandException() {
        CommandContext ctx = new CommandContext();
        sut = new PayCommand(economyServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(mock(CommandBlock.class), ctx)
        );

        Text result = e.getText();
        Text expectedResult = Text.of("Only players can use this command");

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithMissingAmountArgument_ShouldThrowCommandException() {
        CommandContext ctx = new CommandContext();
        ctx.putArg("player", mock(Player.class));

        sut = new PayCommand(economyServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(playerMock, ctx)
        );

        Text result = e.getText();
        Text expectedResult = Text.of("Amount argument is missing");

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithAmountOfZero_ShouldThrowCommandException() {
        CommandContext ctx = new CommandContext();
        ctx.putArg("player", mock(Player.class));
        ctx.putArg("amount", BigDecimal.ZERO);

        sut = new PayCommand(economyServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(playerMock, ctx)
        );

        Text result = e.getText();
        Text expectedResult = Text.of("Amount must be greater than 0");

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithAmountLessThanZero_ShouldThrowCommandException() {
        CommandContext ctx = new CommandContext();
        ctx.putArg("player", mock(Player.class));
        ctx.putArg("amount", BigDecimal.valueOf(-1));

        sut = new PayCommand(economyServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(playerMock, ctx)
        );

        Text result = e.getText();
        Text expectedResult = Text.of("Amount must be greater than 0");

        assertEquals(expectedResult, result);
    }

    // @Test
    // @Tag("Unit")
    // public void execute_WithInsufficientBalance_ShouldThrowCommandException() {
    //     CommandContext ctx = new CommandContext();
    //     ctx.putArg("player", mock(Player.class));
    //     ctx.putArg("amount", BigDecimal.valueOf(100));
    //
    //     sut = new PayCommand(economyServiceMock);
    //
    //     CommandException e = assertThrows(
    //         CommandException.class,
    //         () -> sut.execute(playerMock, ctx)
    //     );
    //
    //     Text result = e.getText();
    //     Text expectedResult = Text.of("Insufficient balance");
    //
    //     assertEquals(expectedResult, result);
    // }

    @Test
    @Tag("Unit")
    public void execute_WithMissingPlayerArgument_ShouldThrowCommandException() {
        CommandContext ctx = new CommandContext();
        ctx.putArg("amount", BigDecimal.valueOf(100));

        sut = new PayCommand(economyServiceMock);

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(playerMock, ctx)
        );

        Text result = e.getText();
        Text expectedResult = Text.of("Player argument is missing");

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Unit")
    public void execute_WithPlayerArgumentTheSameAsExecutor_ShouldThrowCommandException() {
        CommandContext ctx = new CommandContext();
        ctx.putArg("player", playerMock);
        ctx.putArg("amount", BigDecimal.valueOf(100));

        sut = new PayCommand(economyServiceMock);

        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(playerMock, ctx)
        );

        Text result = e.getText();
        Text expectedResult = Text.of("You cannot pay yourself");

        assertEquals(expectedResult, result);
    }

    // @Test
    // @Tag("Unit")
    // public void execute_WithGetOrCreateAccountIssue_ShouldThrowCommandException() {
    //     CommandContext ctx = new CommandContext();
    //     ctx.putArg("player", mock(Player.class));
    //     ctx.putArg("amount", BigDecimal.valueOf(100));
    //
    //     sut = new PayCommand(economyServiceMock);
    //
    //     when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
    //     CommandException e = assertThrows(
    //         CommandException.class,
    //         () -> sut.execute(playerMock, ctx)
    //     );
    //
    //     Text result = e.getText();
    //     Text expectedResult = Text.of("Failed to run command");
    //
    //     assertEquals(expectedResult, result);
    // }

    @Test
    @Tag("Integration")
    public void execute_WithValidData_ShouldUpdateBalances() throws SQLException, CommandException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedUsers();
        when(databaseMock.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection());

        AccountData accountData = new AccountData(databaseMock);
        CurrencyData currencyData = new CurrencyData(databaseMock);
        EconomyService economyService = new TEEconomyService(accountData, currencyData);
        Player toPlayer = mock(Player.class);
        sut = new PayCommand(economyService);

        CommandContext ctx = new CommandContext();
        ctx.putArg("player", toPlayer);
        ctx.putArg("amount", BigDecimal.valueOf(10));

        when(playerMock.getUniqueId()).thenReturn(UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"));
        when(toPlayer.getUniqueId()).thenReturn(UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea"));

        // Act
        CommandResult result = sut.execute(playerMock, ctx);

        // Assert
        try (Connection conn = TestUtils.getConnection()) {
            assert conn != null;

            Statement fromPlayerBalanceStmt = conn.createStatement();
            ResultSet fromPlayerBalanceResult = fromPlayerBalanceStmt.executeQuery(
                "SELECT balance FROM te_balance\n"
                + "WHERE currency_id=1 AND user_id='62694fb0-07cc-4396-8d63-4f70646d75f0'"
            );
            BigDecimal fromPlayerBalance = fromPlayerBalanceResult.getBigDecimal("balance");
            BigDecimal expectedFromPlayerBalance = BigDecimal.valueOf(40);
            assertEquals(expectedFromPlayerBalance, fromPlayerBalance);
        }
    }
}
