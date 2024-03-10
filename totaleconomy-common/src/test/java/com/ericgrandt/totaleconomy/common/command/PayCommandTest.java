package com.ericgrandt.totaleconomy.common.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.ericgrandt.totaleconomy.common.econ.TransactionResult;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.game.CommonSender;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Map;
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
public class PayCommandTest {
    @Mock
    private CommonLogger loggerMock;

    @Mock
    private CommonEconomy economyMock;

    @Mock
    private CommonPlayer playerMock;

    private final CurrencyDto currency = new CurrencyDto(
        1,
        "Dollar",
        "Dollars",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void execute_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        PayCommand sut = new PayCommand(economyMock, currency);

        // Act
        boolean actual = sut.execute(mock(CommonSender.class), null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithNoToPlayerArg_ShouldReturnFalse() {
        // Arrange
        Map<String, CommonParameter<?>> args = Map.of(
            "notToPlayer", new CommonParameter<>(mock(CommonPlayer.class)),
            "amount", new CommonParameter<>(BigDecimal.TEN)
        );

        PayCommand sut = new PayCommand(economyMock, currency);

        // Act
        boolean actual = sut.execute(playerMock, args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithNoAmountArg_ShouldReturnFalse() {
        // Arrange
        Map<String, CommonParameter<?>> args = Map.of(
            "toPlayer", new CommonParameter<>(mock(CommonPlayer.class)),
            "notAmount", new CommonParameter<>(BigDecimal.TEN)
        );

        PayCommand sut = new PayCommand(economyMock, currency);

        // Act
        boolean actual = sut.execute(playerMock, args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithNullToPlayer_ShouldReturnFalse() {
        // Arrange
        CommonPlayer toPlayerMock = mock(CommonPlayer.class);
        when(toPlayerMock.isNull()).thenReturn(true);

        Map<String, CommonParameter<?>> args = Map.of(
            "toPlayer", new CommonParameter<>(toPlayerMock),
            "amount", new CommonParameter<>(BigDecimal.TEN)
        );

        PayCommand sut = new PayCommand(economyMock, currency);

        // Act
        boolean actual = sut.execute(playerMock, args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithSameSenderAndToPlayer_ShouldReturnFalse() {
        // Arrange
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());

        Map<String, CommonParameter<?>> args = Map.of(
            "toPlayer", new CommonParameter<>(playerMock),
            "amount", new CommonParameter<>(BigDecimal.TEN)
        );

        PayCommand sut = new PayCommand(economyMock, currency);

        // Act
        boolean actual = sut.execute(playerMock, args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void execute_WithFailedTransfer_ShouldSendFailureMessageToPlayer() {
        // Arrange
        CommonPlayer toPlayerMock = mock(CommonPlayer.class);
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(toPlayerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(economyMock.transfer(any(UUID.class), any(UUID.class), any(Integer.class), any(BigDecimal.class))).thenReturn(
            new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "Failed"
            )
        );

        Map<String, CommonParameter<?>> args = Map.of(
            "toPlayer", new CommonParameter<>(toPlayerMock),
            "amount", new CommonParameter<>(BigDecimal.TEN)
        );

        PayCommand sut = new PayCommand(economyMock, currency);

        // Act
        boolean actual = sut.execute(playerMock, args);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));
        verify(playerMock).sendMessage(Component.text("Failed"));
        assertTrue(actual);
    }

    @Test
    @Tag("Integration")
    public void execute_WithSuccess_ShouldTransferAndSendMessages() throws SQLException {
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        CommonPlayer toPlayerMock = mock(CommonPlayer.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());
        when(playerMock.getUniqueId()).thenReturn(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );
        when(playerMock.getName()).thenReturn("FromPlayer");
        when(toPlayerMock.getUniqueId()).thenReturn(
            UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea")
        );
        when(toPlayerMock.getName()).thenReturn("ToPlayer");

        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        CurrencyData currencyData = new CurrencyData(databaseMock);
        CommonEconomy economy = new CommonEconomy(loggerMock, accountData, balanceData, currencyData);
        Map<String, CommonParameter<?>> args = Map.of(
            "toPlayer", new CommonParameter<>(toPlayerMock),
            "amount", new CommonParameter<>(BigDecimal.TEN)
        );

        PayCommand sut = new PayCommand(economy, currency);

        // Act
        sut.execute(playerMock, args);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));

        BigDecimal actualPlayerBalance = balanceData.getBalance(UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0"), 1);
        BigDecimal expectedPlayerBalance = BigDecimal.valueOf(40).setScale(2, RoundingMode.DOWN);
        BigDecimal actualTargetBalance = balanceData.getBalance(UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea"), 1);
        BigDecimal expectedTargetBalance = BigDecimal.valueOf(110).setScale(2, RoundingMode.DOWN);

        assertEquals(expectedPlayerBalance, actualPlayerBalance);
        assertEquals(expectedTargetBalance, actualTargetBalance);
        verify(playerMock).sendMessage(
            Component.text(
                "You sent "
            ).append(
                Component.text("$10.00")
            ).append(
                Component.text(" to ")
            ).append(
                Component.text("ToPlayer")
            )
        );
        verify(toPlayerMock).sendMessage(
            Component.text(
                "You received "
            ).append(
                Component.text("$10.00")
            ).append(
                Component.text(" from ")
            ).append(
                Component.text("FromPlayer")
            )
        );
    }
}
