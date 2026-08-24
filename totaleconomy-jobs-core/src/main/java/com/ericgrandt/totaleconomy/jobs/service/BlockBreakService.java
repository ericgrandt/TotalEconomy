package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import com.ericgrandt.totaleconomy.jobs.job.RewardCalculator;

import java.util.UUID;

public class BlockBreakService { // implements EventService {
    private final RewardCalculator rewardCalculator;

    public BlockBreakService(RewardCalculator rewardCalculator) {
        this.rewardCalculator = rewardCalculator;
        // config.getPayoutMultiplier()
        // config.getEntry()
    }

    public void handleBlockBreak(UUID playerId, String jobName, String blockName) {
        var entry = rewardCalculator.getEntry(jobName, JobEnums.ActionType.BLOCK_BREAK, blockName);
        // Parse out the section for the jobName, ACTION_NAME, and blockName
        // Get players current level
        // If present, award xp and payout
    }
}
