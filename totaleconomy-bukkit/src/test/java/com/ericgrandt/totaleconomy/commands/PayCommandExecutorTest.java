package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.command.PayCommand;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.commonimpl.BukkitLogger;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PayCommandExecutorTest {
    @Mock
    private Player playerMock;

    @Mock
    private Logger loggerMock;

    @Mock
    private CommonEconomy economyMock;

    @Mock
    private BukkitWrapper wrapperMock;

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
    public void onCommand_WithInvalidAmountType_ShouldReturnFalse() {
        // Arrange
        PayCommandExecutor sut = new PayCommandExecutor(new PayCommand(economyMock, currency), wrapperMock);

        // Act
        boolean actual = sut.onCommand(
            playerMock,
            mock(Command.class),
            "",
            new String[] {"PlayerName", "not a number"}
        );

        // Assert
        verify(playerMock).sendMessage("Invalid amount specified");
        assertFalse(actual);
    }

    @Test
    @Tag("Integration")
    public void onCommandHandler_ShouldTransferMoney() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        UUID playerUUID = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID targetUUID = UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea");

        Player playerMock = mock(Player.class);
        String playerName = "FromPlayer";
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(playerMock.getName()).thenReturn(playerName);

        Player toPlayerMock = mock(Player.class);
        String toPlayerName = "ToPlayer";
        when(toPlayerMock.getUniqueId()).thenReturn(targetUUID);
        when(toPlayerMock.getName()).thenReturn(toPlayerName);
        when(wrapperMock.getPlayerExact(any(String.class))).thenReturn(toPlayerMock);

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());

        CurrencyDto currency = new CurrencyDto(
            1,
            "Dollar",
            "Dollars",
            "$",
            2,
            true
        );
        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        CurrencyData currencyData = new CurrencyData(databaseMock);

        CommonEconomy economy = new CommonEconomy(
            new BukkitLogger(loggerMock),
            accountData,
            balanceData,
            currencyData
        );
        PayCommandExecutor sut = new PayCommandExecutor(new PayCommand(economy, currency), wrapperMock);

        // Act
        sut.onCommand(playerMock, mock(Command.class), "", new String[] { toPlayerName, "10" });

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));

        BigDecimal actualPlayerBalance = balanceData.getBalance(playerUUID, 1);
        BigDecimal expectedPlayerBalance = BigDecimal.valueOf(40.00).setScale(2, RoundingMode.DOWN);
        BigDecimal actualTargetBalance = balanceData.getBalance(targetUUID, 1);
        BigDecimal expectedTargetBalance = BigDecimal.valueOf(110.00).setScale(2, RoundingMode.DOWN);

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
