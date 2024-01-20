package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.CurrencyImpl;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.impl.UniqueAccountImpl;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.economy.Currency;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private Logger loggerMock;

    @Mock
    private EconomyImpl economyMock;

    private final CurrencyDto currencyDto = new CurrencyDto(1, "Dollar", "Dollars", "$", 2, true);
    private final Currency currency = new CurrencyImpl(currencyDto);

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithAccount_ShouldSendMessageWithBalance() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Map<Currency, BigDecimal> balances = Map.of(currency, BigDecimal.TEN);
        when(economyMock.findOrCreateAccount(any(UUID.class))).thenReturn(
            Optional.of(new UniqueAccountImpl(null, uuid, balances, null, null))
        );

        ServerPlayer playerMock = mock(ServerPlayer.class);
        when(playerMock.uniqueId()).thenReturn(uuid);

        BalanceCommand sut = new BalanceCommand(economyMock, currency);

        // Act
        sut.onCommandHandler(playerMock);

        // Assert
        verify(playerMock).sendMessage(Component.text("Balance: ").append(Component.text("$10.00")));
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithNoAccount_ShouldThrowException() {
        // Arrange
        when(economyMock.findOrCreateAccount(any(UUID.class))).thenReturn(Optional.empty());

        ServerPlayer playerMock = mock(ServerPlayer.class);
        when(playerMock.uniqueId()).thenReturn(UUID.randomUUID());

        BalanceCommand sut = new BalanceCommand(economyMock, currency);

        // Act/Assert
        assertThrows(
            NoSuchElementException.class,
            () -> sut.onCommandHandler(playerMock)
        );
    }

    @Test
    @Tag("Integration")
    public void execute_ShouldReturnPlayerBalanceAndReturnCommandResultSuccess() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        ServerPlayer playerMock = mock(ServerPlayer.class);
        when(playerMock.uniqueId()).thenReturn(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection())
            .thenReturn(TestUtils.getConnection())
            .thenReturn(TestUtils.getConnection());

        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        EconomyImpl economy = new EconomyImpl(loggerMock, currencyDto, accountData, balanceData);

        BalanceCommand sut = new BalanceCommand(economy, currency);

        // Act
        sut.onCommandHandler(playerMock);

        // Assert
        verify(playerMock).sendMessage(Component.text("Balance: ").append(Component.text("$50.00")));
    }
}
