package com.ericgrandt.totaleconomy.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.common.data.dto.BalanceDto;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.services.JobService;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlayerListenerTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void onPlayerJoinHandler_WithAccountAlreadyExisting_ShouldNotCallCreateAccount() {
        // Arrange
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(economyMock.hasAccount(playerMock)).thenReturn(true);

        PlayerListener sut = new PlayerListener(economyMock, jobServiceMock, null);

        // Act
        sut.onPlayerJoinHandler(playerMock);

        // Assert
        verify(economyMock, times(0)).createPlayerAccount(any(Player.class));
    }

    @Test
    @Tag("Unit")
    public void onPlayerJoinHandler_WithNoAccountAlreadyExisting_ShouldCallCreateAccount() {
        // Arrange
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(economyMock.hasAccount(playerMock)).thenReturn(false);

        PlayerListener sut = new PlayerListener(economyMock, jobServiceMock, null);

        // Act
        sut.onPlayerJoinHandler(playerMock);

        // Assert
        verify(economyMock, times(1)).createPlayerAccount(playerMock);
    }

    @Test
    @Tag("Integration")
    public void onPlayerJoinHandler_ShouldCreateNewAccountAndBalances() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();
        TestUtils.seedJobs();

        CurrencyDto currencyDto = new CurrencyDto(0, "", "", "", 0, true);
        BalanceData balanceDataMock = mock(BalanceData.class);
        Database databaseMock = mock(Database.class);
        Player playerMock = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());

        AccountData accountData = new AccountData(databaseMock);
        JobData jobData = new JobData(databaseMock);
        EconomyImpl economy = new EconomyImpl(loggerMock, true, currencyDto, accountData, balanceDataMock);
        JobService jobServiceMock = new JobService(loggerMock, jobData);
        PlayerListener sut = new PlayerListener(economy, jobServiceMock, null);

        // Act
        sut.onPlayerJoinHandler(playerMock);

        // Assert
        assertAccountsAreEqualOnPlayerJoinHandler(playerId);
        assertJobExperienceIsAddedOnPlayerJoinHandler(playerId);
    }

    private void assertAccountsAreEqualOnPlayerJoinHandler(UUID playerId) throws SQLException {
        AccountDto actualAccount = TestUtils.getAccount(playerId);
        AccountDto expectedAccount = new AccountDto(
            playerId.toString(),
            actualAccount.created()
        );

        BalanceDto actualBalance = TestUtils.getBalanceForAccountId(playerId, 1);
        BalanceDto expectedBalance = new BalanceDto(
            actualBalance.id(),
            playerId.toString(),
            1,
            BigDecimal.valueOf(100.50).setScale(2, RoundingMode.DOWN)
        );

        assertEquals(expectedAccount, actualAccount);
        assertEquals(expectedBalance, actualBalance);
    }

    private void assertJobExperienceIsAddedOnPlayerJoinHandler(UUID accountId) throws SQLException {
        List<JobExperienceDto> actualJobExperience = TestUtils.getExperienceForJobs(accountId);

        assert actualJobExperience.size() == 2;
    }
}
