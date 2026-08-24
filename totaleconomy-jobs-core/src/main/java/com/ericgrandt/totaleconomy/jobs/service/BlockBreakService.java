package com.ericgrandt.totaleconomy.jobs.service;

import com.ericgrandt.totaleconomy.jobs.config.Config;

import java.util.UUID;

public class BlockBreakService { // implements EventService {
    private final Config config;

    private final String ACTION_NAME = "block_break";

    public BlockBreakService(Config config) {
        this.config = config;
        // config.getPayoutMultiplier()
        // config.getEntry()
    }

    public void handleBlockBreak(UUID playerId, String jobName, String blockName) {
        // Parse out the section for the jobName, ACTION_NAME, and blockName
        // Get players current level
        // If present, award xp and payout
    }
}
