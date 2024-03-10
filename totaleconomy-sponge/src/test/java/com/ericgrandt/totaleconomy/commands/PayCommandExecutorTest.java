package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
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
import com.ericgrandt.totaleconomy.commonimpl.SpongeLogger;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@ExtendWith(MockitoExtension.class)
public class PayCommandExecutorTest {
    @Mock
    private Logger loggerMock;

    @Mock
    private SpongeWrapper wrapperMock;

    @Mock
    private Parameter.Value<ServerPlayer> toPlayerParamMock;

    @Mock
    private Parameter.Value<Double> amountParamMock;

    @Test
    @Tag("Integration")
    public void execute_ShouldTransferMoney() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        UUID playerUUID = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID targetUUID = UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea");

        ServerPlayer playerMock = mock(ServerPlayer.class);
        String playerName = "FromPlayer";
        when(playerMock.uniqueId()).thenReturn(playerUUID);
        when(playerMock.name()).thenReturn(playerName);

        ServerPlayer toPlayerMock = mock(ServerPlayer.class);
        String toPlayerName = "ToPlayer";
        when(toPlayerMock.uniqueId()).thenReturn(targetUUID);
        when(toPlayerMock.name()).thenReturn(toPlayerName);

        Database databaseMock = mock(Database.class);
        CommandContext commandContextMock = mock(CommandContext.class, RETURNS_DEEP_STUBS);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());
        when(playerMock.uniqueId()).thenReturn(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );
        when(commandContextMock.cause().root()).thenReturn(playerMock);

        when(wrapperMock.playerParameter(any(String.class))).thenReturn(toPlayerParamMock);
        when(wrapperMock.doubleParameter(any(String.class))).thenReturn(amountParamMock);
        when(commandContextMock.requireOne(toPlayerParamMock)).thenReturn(toPlayerMock);
        when(commandContextMock.requireOne(amountParamMock)).thenReturn(10d);

        CurrencyDto defaultCurrency = new CurrencyDto(
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
            new SpongeLogger(loggerMock),
            accountData,
            balanceData,
            currencyData
        );

        PayCommandExecutor sut = new PayCommandExecutor(economy, defaultCurrency, wrapperMock);

        // Act
        sut.execute(commandContextMock);

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
