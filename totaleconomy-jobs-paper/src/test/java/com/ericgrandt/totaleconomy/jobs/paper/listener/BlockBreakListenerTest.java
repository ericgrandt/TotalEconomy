package com.ericgrandt.totaleconomy.jobs.paper.listener;

import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.common.testutils.TestTaskRunner;
import com.ericgrandt.totaleconomy.common.testutils.TestUtils;
import com.ericgrandt.totaleconomy.jobs.data.ActiveJobData;
import com.ericgrandt.totaleconomy.jobs.data.DatabaseSetup;
import com.ericgrandt.totaleconomy.jobs.data.JobExperienceData;
import com.ericgrandt.totaleconomy.jobs.dto.GetJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.job.JobCalculator;
import com.ericgrandt.totaleconomy.jobs.model.JobExperience;
import com.ericgrandt.totaleconomy.jobs.paper.mapper.ExceptionMapper;
import com.ericgrandt.totaleconomy.jobs.service.BlockBreakService;
import com.ericgrandt.totaleconomy.jobs.service.JobService;
import com.ericgrandt.totaleconomy.jobs.testutils.TestSeeder;
import com.ericgrandt.totaleconomy.service.TEEconomyService;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.UUID;

import static com.ericgrandt.totaleconomy.jobs.paper.TestUtils.buildTestConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BlockBreakListenerTest {
    @Mock
    private Logger loggerMock;

    private final TestTaskRunner taskRunner = new TestTaskRunner();

    // TODO: Probably should be testing that the money reward is paid out, though this test already has a ton of
    //  setup. Think about it.
    @Test
    @Tag("Integration")
    public void onBlockBreak_WithSuccess_ShouldProvideReward() throws SQLException {
        // Arrange
        var dataSource = TestUtils.startTestDb(true, DatabaseSetup::init);
        var activeJob = TestSeeder.seedActiveJob(dataSource);
        TestSeeder.seedJobExperience(dataSource);

        var playerId = activeJob.playerId();
        var config = buildTestConfig();
        var transactionUtil = new TransactionUtil(dataSource);
        var exceptionMapper = new ExceptionMapper(loggerMock);
        var economyServiceMock = mock(TEEconomyService.class);
        var jobCalculator = new JobCalculator(config);
        var jobExperienceData = new JobExperienceData();
        var jobService = new JobService(transactionUtil, new ActiveJobData(), jobExperienceData);
        var blockBreakService = new BlockBreakService(
            transactionUtil,
            jobExperienceData,
            economyServiceMock,
            jobCalculator
        );

        var event = mock(BlockBreakEvent.class, Answers.RETURNS_DEEP_STUBS);
        when(event.getPlayer().getUniqueId()).thenReturn(UUID.fromString(playerId));

        var sut = new BlockBreakListener(taskRunner, exceptionMapper, blockBreakService, jobService);

        // Act
        sut.onBlockBreak(event);

        var actual = jobService.getJobExperience(new GetJobExperienceDto(UUID.fromString(playerId), activeJob.jobId()));
        var expected = new JobExperience(UUID.fromString(playerId), activeJob.jobId(), 10);

        // Assert
        assertEquals(expected, actual);
    }
}
