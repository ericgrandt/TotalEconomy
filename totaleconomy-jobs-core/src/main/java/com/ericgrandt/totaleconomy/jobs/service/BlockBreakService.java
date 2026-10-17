package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.api.service.EconomyService;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import com.ericgrandt.totaleconomy.jobs.data.JobExperienceData;
import com.ericgrandt.totaleconomy.jobs.dto.GetJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.dto.HandleActionDto;
import com.ericgrandt.totaleconomy.jobs.dto.Status;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.job.JobCalculator;
import com.ericgrandt.totaleconomy.jobs.model.JobExperience;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class BlockBreakService implements ActionService {
    private final TransactionUtil transactionUtil;
    private final JobExperienceData jobExperienceData;
    private final EconomyService<?> economyService;
    private final JobCalculator jobCalculator;
    private final Config config;

    public BlockBreakService(
        TransactionUtil transactionUtil,
        JobExperienceData jobExperienceData,
        EconomyService<?> economyService,
        JobCalculator jobCalculator,
        Config config
    ) {
        this.transactionUtil = transactionUtil;
        this.jobExperienceData = jobExperienceData;
        this.economyService = economyService;
        this.jobCalculator = jobCalculator;
        this.config = config;
    }

    @Override
    public HandleActionDto handleAction(UUID playerId, String jobName, String blockName) {
        var entryOpt = config.getEntry(jobName, JobEnums.ActionType.BLOCK_BREAK, blockName);
        if (entryOpt.isEmpty()) {
            return new HandleActionDto(Status.NOENTRY, 0, BigDecimal.ZERO, false);
        }
        var entry = entryOpt.get();

        try {
            return transactionUtil.runInTransaction(conn -> {
                var req = new GetJobExperienceDto(playerId, jobName);
                var jobExperience = jobExperienceData.getJobExperience(conn, req)
                    .orElse(new JobExperience(null, "testJob", 0));

                var preRewardLevel = jobCalculator.calculateLevelFromExp(jobExperience.experience());
                var updatedJobExperience = jobExperienceData.upsertJobExperience(
                    conn,
                    new UpsertJobExperienceDto(playerId, jobName, entry.xp())
                );
                economyService.deposit(playerId, entry.payout());
                var postRewardLevel = jobCalculator.calculateLevelFromExp(updatedJobExperience.experience());

                return new HandleActionDto(
                    Status.SUCCESS,
                    entry.xp(),
                    entry.payout(),
                    postRewardLevel > preRewardLevel
                );
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while handling block break event", e);
        }
    }
}
