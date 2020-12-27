package com.erigitic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.erigitic.data.Database;
import com.erigitic.services.TEEconomyService;
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
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;

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
    public void execute_WithMissingArgument_ShouldThrowCommandException() {
        CommandContext ctx = new CommandContext();
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
}
