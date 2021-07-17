package com.ericgrandt.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.TestUtils;
import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.data.Database;
import com.ericgrandt.domain.TECurrency;
import com.ericgrandt.services.AccountService;
import com.ericgrandt.services.TEEconomyService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.block.entity.CommandBlock;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    private BalanceCommand sut;

    @Mock
    private Database databaseMock;

    @Mock
    private TEEconomyService economyServiceMock;

    @Mock
    private Player playerMock;

    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerCommandSource_ShouldThrowCommandException() {
        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(mock(CommandBlock.class));
        sut = new BalanceCommand(economyServiceMock, mock(AccountService.class));

        CommandException e = assertThrows(
            CommandException.class,
            () -> sut.execute(ctx)
        );

        String result = e.getMessage();
        String expectedResult = "Only players can use this command";

        assertEquals(expectedResult, result);
    }

    @Test
    @Tag("Integration")
    public void execute_WithValidData_ShouldReturnCommandResultSuccess() throws SQLException, CommandException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedUser();
        when(databaseMock.getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection());
        when(playerMock.uniqueId()).thenReturn(UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"));

        Currency currency = new TECurrency(1, "Dollar", "Dollars", "$", true);
        AccountData accountData = new AccountData(null, databaseMock);
        CurrencyData currencyData = new CurrencyData(null, databaseMock);
        AccountService accountService = new AccountService(accountData);
        EconomyService economyService = new TEEconomyService(accountData, currencyData);
        sut = new BalanceCommand(economyService, accountService);

        CommandContext ctx = mock(CommandContext.class);
        when(ctx.cause()).thenReturn(mock(CommandCause.class));
        when(ctx.cause().root()).thenReturn(playerMock);

        // Act
        CommandResult result = sut.execute(ctx);

        // Assert
        verify(playerMock).sendMessage(
            Component.text("Balance: ", NamedTextColor.GRAY)
                .append(currency.format(BigDecimal.valueOf(123)).color(NamedTextColor.GOLD))
        );
        assertTrue(result.isSuccess());
    }
}
