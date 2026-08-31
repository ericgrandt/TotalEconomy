package com.ericgrandt.totaleconomy.jobs.job;

import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;

import java.util.Optional;

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

    // TODO: This should be moved to the config package or Config class
    public Optional<Config.Job.Action.Entry> getEntry(
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
        return config.jobs().stream().filter(j -> j.id().equals(jobName)).findFirst();
    }

    private Optional<Config.Job.Action> getAction(Config.Job job, JobEnums.ActionType actionType) {
        return Optional.ofNullable(job.actionsMap().get(actionType));
    }
}
