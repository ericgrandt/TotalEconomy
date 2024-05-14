package com.ericgrandt.totaleconomy.common.listeners;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.TestUtils;
import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.Database;
import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.data.dto.BalanceDto;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.econ.TransactionResult;
import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.AddExperienceResponse;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import com.ericgrandt.totaleconomy.common.services.JobService;
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
public class CommonJobListenerTest {
    @Mock
    private CommonEconomy economyMock;

    @Mock
    private JobService jobServiceMock;

    @Mock
    private Database databaseMock;

    @Mock
    private CommonLogger loggerMock;

    @Mock
    private CommonPlayer playerMock;

    @Test
    @Tag("Unit")
    public void handleAction_WithLevelUp_ShouldSendLevelUpMessage() {
        // Arrange
        when(jobServiceMock.getJobReward(any(GetJobRewardRequest.class))).thenReturn(
            new GetJobRewardResponse(UUID.randomUUID().toString(), BigDecimal.ONE, 10)
        );
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(jobServiceMock.addExperience(any(AddExperienceRequest.class))).thenReturn(
            new AddExperienceResponse("miner", 2, true)
        );
        when(economyMock.deposit(any(UUID.class), any(Integer.class), any(BigDecimal.class), any(Boolean.class)))
            .thenReturn(
                new TransactionResult(TransactionResult.ResultType.SUCCESS, "")
            );

        JobEvent jobEvent = new JobEvent(playerMock, "break", "coal_ore");
        CommonJobListener sut = new CommonJobListener(economyMock, jobServiceMock, 1);

        // Act
        sut.handleAction(jobEvent);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));

        verify(playerMock, times(1)).sendMessage(any(Component.class));
    }

    @Test
    @Tag("Unit")
    public void handleAction_WithNoLevelUp_ShouldNotSendLevelUpMessage() {
        // Arrange
        when(jobServiceMock.getJobReward(any(GetJobRewardRequest.class))).thenReturn(
            new GetJobRewardResponse(UUID.randomUUID().toString(), BigDecimal.ONE, 10)
        );
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(jobServiceMock.addExperience(any(AddExperienceRequest.class))).thenReturn(
            new AddExperienceResponse("miner", 1, false)
        );
        when(economyMock.deposit(any(UUID.class), any(Integer.class), any(BigDecimal.class), any(Boolean.class)))
            .thenReturn(
                new TransactionResult(TransactionResult.ResultType.SUCCESS, "")
            );

        JobEvent jobEvent = new JobEvent(playerMock, "break", "coal_ore");
        CommonJobListener sut = new CommonJobListener(economyMock, jobServiceMock, 1);

        // Act
        sut.handleAction(jobEvent);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));

        verify(playerMock, times(0)).sendMessage(any(Component.class));
    }

    @Test
    @Tag("Integration")
    public void handleAction_WithBreakActionAndJobReward_ShouldRewardExperienceAndMoney() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobActions();
        TestUtils.seedJobRewards();
        TestUtils.seedJobExperience();

        UUID playerId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());
        when(playerMock.getUniqueId()).thenReturn(playerId);

        JobData jobData = new JobData(loggerMock, databaseMock);
        JobService jobService = new JobService(jobData);
        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        CurrencyData currencyData = new CurrencyData(databaseMock);

        CommonEconomy economy = new CommonEconomy(
            loggerMock,
            accountData,
            balanceData,
            currencyData
        );

        JobEvent jobEvent = new JobEvent(playerMock, "break", "coal_ore");
        CommonJobListener sut = new CommonJobListener(economy, jobService, 1);

        // Act
        sut.handleAction(jobEvent);

        // Assert
        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS));

        BalanceDto actualBalance = TestUtils.getBalanceForAccountId(playerId, 1);
        BalanceDto expectedBalance = new BalanceDto(
            "ab661384-11f5-41e1-a5e6-6fa93305d4d1",
            "62694fb0-07cc-4396-8d63-4f70646d75f0",
            1,
            BigDecimal.valueOf(50.50).setScale(2, RoundingMode.DOWN)
        );
        JobExperience actualExperience = TestUtils.getJobExperience(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf"
        );
        JobExperience expectedExperience = new JobExperience(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            "62694fb0-07cc-4396-8d63-4f70646d75f0",
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            51
        );

        assertEquals(expectedBalance, actualBalance);
        assertThat(actualExperience).usingRecursiveComparison().isEqualTo(expectedExperience);
        verify(playerMock, times(0)).sendMessage(any(Component.class));
    }
}
