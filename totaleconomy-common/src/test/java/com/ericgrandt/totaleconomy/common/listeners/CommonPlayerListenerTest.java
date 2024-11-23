package com.ericgrandt.totaleconomy.common.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.domain.Account;
import com.ericgrandt.totaleconomy.common.domain.Balance;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.services.JobService;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommonPlayerListenerTest {
    @Mock
    private Database databaseMock;

    @Mock
    private CommonPlayer playerMock;

    @Test
    @Tag("Integration")
    public void onPlayerJoinHandler_ShouldCreateNewAccountAndBalances() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();
        TestUtils.seedJobs();

        UUID playerId = UUID.randomUUID();
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());

        JobData jobData = new JobData(null, databaseMock);
        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        CurrencyData currencyData = new CurrencyData(databaseMock);

        CommonEconomy economy = new CommonEconomy(
            null,
            accountData,
            balanceData,
            currencyData
        );
        JobService jobService = new JobService(jobData);

        CommonPlayerListener sut = new CommonPlayerListener(economy, Optional.of(jobService));

        // Act
        sut.onPlayerJoin(playerMock);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10,TimeUnit.SECONDS));
        assertAccountsAreEqualOnPlayerJoinHandler(playerId);
        assertJobExperienceIsAddedOnPlayerJoinHandler(playerId);
    }

    private void assertAccountsAreEqualOnPlayerJoinHandler(UUID playerId) throws SQLException {
        Account actualAccount = TestUtils.getAccount(playerId);
        Account expectedAccount = new Account(
            playerId.toString(),
            actualAccount.created()
        );

        Balance actualBalance = TestUtils.getBalanceForAccountId(playerId, 1);
        Balance expectedBalance = new Balance(
            actualBalance.id(),
            playerId.toString(),
            1,
            BigDecimal.valueOf(100.50).setScale(2, RoundingMode.DOWN)
        );

        assertEquals(expectedAccount, actualAccount);
        assertEquals(expectedBalance, actualBalance);
    }

    private void assertJobExperienceIsAddedOnPlayerJoinHandler(UUID accountId) throws SQLException {
        List<JobExperience> actualJobExperience = TestUtils.getExperienceForJobs(accountId);

        assert actualJobExperience.size() == 2;
    }
}
