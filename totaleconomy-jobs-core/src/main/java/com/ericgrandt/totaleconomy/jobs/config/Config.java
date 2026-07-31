package com.ericgrandt.totaleconomy.jobs.config;

import com.ericgrandt.totaleconomy.jobs.config.JobEnums.ActionType;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums.PayoutMultiplierType;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums.XPCurveType;

import java.math.BigDecimal;
import java.util.List;

public record Config(List<Job> jobs, Settings settings) {
    public record Job(
        String id,
        String displayName,
        String description,
        List<Action> actions
    ) {
        public record Action(
            ActionType type,
            List<Entry> entries
        ) {
            public record Entry(
                String material,
                int xp,
                BigDecimal payout
            ) {
            }
        }
    }

    public record Settings(
        int maxLevel,
        XPCurve xpCurve,
        PayoutMultiplier payoutMultiplier
    ) {
        public record XPCurve(
            XPCurveType type,
            int baseXP
        ) {

        }

        public record PayoutMultiplier(
            PayoutMultiplierType type,
            int base,
            double perLevel
        ) {

        }
    }
}
