package com.ericgrandt.totaleconomy.jobs.config;

import com.ericgrandt.totaleconomy.jobs.config.JobEnums.ActionType;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums.PayoutMultiplierType;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums.XPCurveType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: If it becomes an issue, List<Job> can become a Map for quicker/easier lookup
public record Config(List<Job> jobs, Settings settings) {
    public record Job(
        String id,
        String displayName,
        String description,
        Map<ActionType, Action> actionsMap
    ) {
        public record Action(
            ActionType type,
            Map<String, Entry> entryMap
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
            double base,
            double perLevel
        ) {

        }
    }

    public Optional<Job.Action.Entry> getEntry(
        String jobName,
        JobEnums.ActionType actionType,
        String materialName
    ) {
        var job = getJob(jobName);
        if (job.isEmpty()) {
            return Optional.empty();
        }

        var action = getAction(job.get(), actionType);
        return action.map(value -> value.entryMap().get(materialName));
    }

    private Optional<Config.Job> getJob(String jobName) {
        return this.jobs().stream().filter(j -> j.id().equals(jobName)).findFirst();
    }

    private Optional<Config.Job.Action> getAction(Config.Job job, JobEnums.ActionType actionType) {
        return Optional.ofNullable(job.actionsMap().get(actionType));
    }
}
