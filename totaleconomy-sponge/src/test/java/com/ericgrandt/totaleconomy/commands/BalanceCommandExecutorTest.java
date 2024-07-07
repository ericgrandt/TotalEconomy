package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
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
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandExecutorTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Integration")
    public void execute_ShouldSendMessageWithBalanceToPlayer() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        ServerPlayer playerMock = mock(ServerPlayer.class);
        CommandContext commandContextMock = mock(CommandContext.class, RETURNS_DEEP_STUBS);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).thenReturn(
            TestUtils.getConnection(),
            TestUtils.getConnection()
        );
        when(playerMock.uniqueId()).thenReturn(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );
        when(commandContextMock.cause().root()).thenReturn(playerMock);

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
        CommonEconomy economy = new CommonEconomy(new SpongeLogger(loggerMock), accountData, balanceData, currencyData);

        BalanceCommandExecutor sut = new BalanceCommandExecutor(economy, defaultCurrency, mock(SpongeWrapper.class));

        // Act
        sut.execute(commandContextMock);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));
        verify(playerMock).sendMessage(Component.text("Balance: ").append(Component.text("$50.00")));
    }
}
