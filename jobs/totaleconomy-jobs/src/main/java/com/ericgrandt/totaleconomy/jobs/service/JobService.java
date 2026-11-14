package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.jobs.data.ActiveJobData;
import com.ericgrandt.totaleconomy.jobs.data.JobExperienceData;
import com.ericgrandt.totaleconomy.jobs.dto.GetJobExperienceDto;
import com.ericgrandt.totaleconomy.jobs.dto.UpsertActiveJobDto;
import com.ericgrandt.totaleconomy.jobs.exception.ActiveJobNotFoundException;
import com.ericgrandt.totaleconomy.jobs.exception.JobExperienceNotFoundException;
import com.ericgrandt.totaleconomy.jobs.model.ActiveJob;
import com.ericgrandt.totaleconomy.jobs.model.JobExperience;

import java.sql.SQLException;
import java.util.UUID;

public class JobService {
    private final TransactionUtil transactionUtil;
    private final ActiveJobData activeJobData;
    private final JobExperienceData jobExperienceData;

    public JobService(
        TransactionUtil transactionUtil,
        ActiveJobData activeJobData,
        JobExperienceData jobExperienceData
    ) {
        this.transactionUtil = transactionUtil;
        this.activeJobData = activeJobData;
        this.jobExperienceData = jobExperienceData;
    }

    // TODO: Test
    // TODO: Verify the requested job actually exists in the configuration
    public ActiveJob setActiveJob(UpsertActiveJobDto req) {
        try {
            return transactionUtil.runInTransaction(conn -> activeJobData.upsertActiveJob(conn, req));
        } catch (SQLException e) {
            throw new DatabaseException("database exception while setting active job", e);
        }
    }

    // TODO: Think about caching? Over-optimization?
    public ActiveJob getActiveJob(UUID playerId) {
        try {
            return transactionUtil.runInTransaction(conn -> activeJobData.getActiveJob(conn, playerId)
                .orElseThrow(ActiveJobNotFoundException::new));
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting active job", e);
        }
    }

    public JobExperience getJobExperience(GetJobExperienceDto req) {
        try {
            return transactionUtil.runInTransaction(conn -> jobExperienceData.getJobExperience(
                    conn,
                    req
                )
                .orElseThrow(JobExperienceNotFoundException::new));
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting job experience", e);
        }
    }
}
