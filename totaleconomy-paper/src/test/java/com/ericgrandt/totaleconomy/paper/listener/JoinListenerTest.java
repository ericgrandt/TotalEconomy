package com.ericgrandt.totaleconomy.paper.listener;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.paper.util.TestTaskRunner;
import com.ericgrandt.totaleconomy.service.CacheService;
import com.ericgrandt.totaleconomy.service.TEEconomyService;
import com.ericgrandt.totaleconomy.testutils.TestUtils;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class JoinListenerTest {
    @Mock
    private Logger loggerMock;

    @Mock
    private PlayerJoinEvent playerJoinEventMock;

    private final AsyncTaskRunner taskRunner = new TestTaskRunner();
    private final CurrencyData currencyData = new CurrencyData();
    private final AccountData accountData = new AccountData();

    @Test
    @Tag("Integration")
    public void onPlayerJoin_WithSuccess_ShouldCreateAccountForEachCurrency() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true);
        var defaultCurrency = TestUtils.seedDefaultCurrency(dataSource);
        var currency = TestUtils.seedCurrency(dataSource);
        var accountDefault = TestUtils.seedAccount(dataSource, null);
        var account = TestUtils.seedAccount(dataSource, currency.code());

        var transactionUtil = new TransactionUtil(dataSource);
        var cacheService = new CacheService(transactionUtil, currencyData);
        var economyService = new TEEconomyService(transactionUtil, cacheService, currencyData, accountData);

        var sut = new JoinListener(taskRunner, loggerMock, economyService);

        // Act
        sut.onPlayerJoin(playerJoinEventMock);

        var actualDefault = economyService.getAccountBalance(
            UUID.fromString(accountDefault.playerId()),
            defaultCurrency.code()
        );
        var expectedDefault = new TEAccount(
            UUID.fromString(accountDefault.playerId()),
            defaultCurrency.code(),
            defaultCurrency.startingBalance()
        );

        var actual = economyService.getAccountBalance(UUID.fromString(account.playerId()), currency.code());
        var expected = new TEAccount(
            UUID.fromString(account.playerId()),
            currency.code(),
            currency.startingBalance()
        );

        // Assert
        assertEquals(
            0,
            expectedDefault.balance().compareTo(actualDefault.balance().setScale(2, RoundingMode.DOWN))
        );
        assertEquals(expectedDefault.currencyCode(), actualDefault.currency().code());

        assertEquals(
            0,
            expected.balance().compareTo(actual.balance().setScale(2, RoundingMode.DOWN))
        );
        assertEquals(expected.currencyCode(), actual.currency().code());
    }
}
