package com.ericgrandt.totaleconomy.jobs.job;

import com.ericgrandt.totaleconomy.jobs.config.Config;

public class JobCalculator {
    private final Config config;

    public JobCalculator(Config config) {
        this.config = config;
    }

    public double calculatePayout(int level) {
        var payoutMultiplier = config.settings().payoutMultiplier();
        var normalizedLevel = Math.max(0, level - 1);
        return payoutMultiplier.base() + (normalizedLevel * payoutMultiplier.perLevel());
    }

    public int calculateLevelFromExp(int experience) {
        int baseExp = config.settings().xpCurve().baseXP();
        int level = (int) Math.floor(Math.sqrt((double) experience / baseExp)) + 1;

        return Math.min(config.settings().maxLevel(), level);
    }
}
