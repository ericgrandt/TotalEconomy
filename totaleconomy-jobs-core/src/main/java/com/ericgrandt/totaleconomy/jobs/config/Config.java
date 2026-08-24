package com.ericgrandt.totaleconomy.jobs.config;

import com.ericgrandt.totaleconomy.jobs.config.JobEnums.ActionType;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums.PayoutMultiplierType;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums.XPCurveType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO/NOTE: Would it be better to make actions a Map<ActionType, Entry>?
public record Config(List<Job> jobs, Settings settings) {
    public record Job(
        String id,
        String displayName,
        String description,
        List<Action> actions
    ) {
        public record Action(
            ActionType type,
            List<Entry> entries, // List for iteration over all Entry objects
            Map<String, Entry> entryMap // Map for quicker lookup by action type
        ) {
            public Action(ActionType type, List<Entry> entries) {
                this(type, entries, buildEntryMap(entries));
            }

            public record Entry(
                String material,
                int xp,
                BigDecimal payout
            ) {
            }

            private static Map<String, Entry> buildEntryMap(List<Entry> entries) {
                return entries.stream().collect(Collectors.toMap(
                    Entry::material,
                    entry -> entry,
                    (oldEntry, _) -> oldEntry
                ));
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
            double base,
            double perLevel
        ) {

        }
    }
}
