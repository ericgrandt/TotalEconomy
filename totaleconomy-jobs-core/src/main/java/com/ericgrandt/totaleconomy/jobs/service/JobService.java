package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.common.data.TransactionUtil;
import com.ericgrandt.totaleconomy.jobs.data.ActiveJobData;
import com.ericgrandt.totaleconomy.jobs.exception.ActiveJobNotFoundException;
import com.ericgrandt.totaleconomy.jobs.model.ActiveJob;

import java.sql.SQLException;
import java.util.UUID;

public class JobService {
    private final TransactionUtil transactionUtil;
    private final ActiveJobData activeJobData;

    public JobService(TransactionUtil transactionUtil, ActiveJobData activeJobData) {
        this.transactionUtil = transactionUtil;
        this.activeJobData = activeJobData;
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
}
