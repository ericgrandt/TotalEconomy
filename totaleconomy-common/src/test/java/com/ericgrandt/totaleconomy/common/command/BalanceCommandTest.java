package com.ericgrandt.totaleconomy.common.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private CommonEconomy economyMock;

    @Mock
    private CommonPlayer playerMock;

    @Mock
    private CommonLogger loggerMock;

    private final CurrencyDto currency = new CurrencyDto(
        1,
        "singular",
        "plural",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        BalanceCommand sut = new BalanceCommand(economyMock, currency);

        // Act
        boolean actual = sut.execute(mock(CommonSender.class), null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithNoBalance_ShouldSendMessage() {
        // Arrange
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(economyMock.getBalance(any(UUID.class), any(Integer.class))).thenReturn(null);

        BalanceCommand sut = new BalanceCommand(economyMock, currency);

        // Act
        sut.execute(playerMock, null);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10,TimeUnit.SECONDS));
        verify(playerMock).sendMessage(Component.text("No balance found"));
    }

    @Test
    @Tag("Unit")
    public void execute_WithBalance_ShouldSendMessage() {
        // Arrange
        BigDecimal balance = BigDecimal.valueOf(100).setScale(2, RoundingMode.DOWN);

        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(economyMock.getBalance(any(UUID.class), any(Integer.class))).thenReturn(balance);
        when(economyMock.format(any(CurrencyDto.class), any(BigDecimal.class))).thenReturn(
            Component.text("$" + balance)
        );

        BalanceCommand sut = new BalanceCommand(economyMock, currency);

        // Act
        sut.execute(playerMock, null);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10,TimeUnit.SECONDS));
        verify(playerMock).sendMessage(Component.text("Balance: ").append(Component.text("$100.00")));
    }

    @Test
    @Tag("Integration")
    public void execute_ShouldSendPlayerTheirBalance() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(TestUtils.getConnection());
        when(playerMock.getUniqueId()).thenReturn(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );

        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        CurrencyData currencyData = new CurrencyData(databaseMock);
        CommonEconomy economy = new CommonEconomy(loggerMock, accountData, balanceData, currencyData);

        BalanceCommand sut = new BalanceCommand(economy, currency);

        // Act
        sut.execute(playerMock, null);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));
        verify(playerMock).sendMessage(Component.text("Balance: ").append(Component.text("$50.00")));
    }
}
