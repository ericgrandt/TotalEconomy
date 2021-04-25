package com.erigitic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.erigitic.TestUtils;
import com.erigitic.data.AccountData;
import com.erigitic.data.CurrencyData;
import com.erigitic.data.Database;
import com.erigitic.domain.TECurrency;
import com.erigitic.services.AccountService;
import com.erigitic.services.TEEconomyService;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.command.CommandResult;
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

    // @Test
    // @Tag("Unit")
    // public void execute_WithNonPlayerCommandSource_ShouldThrowCommandException() {
    //     CommandContext ctx = new CommandContext();
    //     sut = new BalanceCommand(economyServiceMock, mock(AccountService.class));
    //
    //     CommandException e = assertThrows(
    //         CommandException.class,
    //         () -> sut.execute(mock(CommandBlock.class), ctx)
    //     );
    //
    //     Text result = e.getText();
    //     Text expectedResult = Text.of("Only players can use this command");
    //
    //     assertEquals(expectedResult, result);
    // }
    //
    // @Test
    // @Tag("Integration")
    // public void execute_WithValidData_ShouldReturnCommandResultSuccess() throws SQLException, CommandException {
    //     TestUtils.resetDb();
    //     TestUtils.seedCurrencies();
    //     TestUtils.seedUser();
    //     when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());
    //     when(playerMock.getUniqueId()).thenReturn(UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"));
    //
    //     Currency currency = new TECurrency(1, "Dollar", "Dollars", "$", true);
    //     AccountData accountData = new AccountData(databaseMock);
    //     CurrencyData currencyData = new CurrencyData(databaseMock);
    //     AccountService accountService = new AccountService(accountData);
    //     EconomyService economyService = new TEEconomyService(accountData, currencyData);
    //     sut = new BalanceCommand(economyService, accountService);
    //
    //     CommandContext ctx = new CommandContext();
    //     ctx.putArg("currencyName", currency);
    //
    //     CommandResult result = sut.execute(playerMock, ctx);
    //     CommandResult expected = CommandResult.success();
    //
    //     verify(playerMock).sendMessage(
    //         Text.of(
    //             TextColors.GRAY,
    //             "Balance: ",
    //             TextColors.GOLD,
    //             currency.format(BigDecimal.valueOf(123))
    //         )
    //     );
    //     assertEquals(expected, result);
    // }
    //
    // @Test
    // @Tag("Integration")
    // public void execute_WithInvalidCurrency_ShouldUseDefaultCurrencyAndReturnCommandResultSuccess() throws CommandException, SQLException {
    //     TestUtils.resetDb();
    //     TestUtils.seedCurrencies();
    //     TestUtils.seedUser();
    //     when(databaseMock.getConnection())
    //         .thenReturn(TestUtils.getConnection())
    //         .thenReturn(TestUtils.getConnection());
    //     when(playerMock.getUniqueId()).thenReturn(UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"));
    //
    //     Currency defaultCurrency = new TECurrency(1, "Dollar", "Dollars", "$", true);
    //     AccountData accountData = new AccountData(databaseMock);
    //     CurrencyData currencyData = new CurrencyData(databaseMock);
    //     AccountService accountService = new AccountService(accountData);
    //     EconomyService economyService = new TEEconomyService(accountData, currencyData);
    //     sut = new BalanceCommand(economyService, accountService);
    //
    //     CommandResult result = sut.execute(playerMock, new CommandContext());
    //     CommandResult expected = CommandResult.success();
    //
    //     verify(playerMock).sendMessage(
    //         Text.of(
    //             TextColors.GRAY,
    //             "Balance: ",
    //             TextColors.GOLD,
    //             defaultCurrency.format(BigDecimal.valueOf(123))
    //         )
    //     );
    //     assertEquals(expected, result);
    // }
}
