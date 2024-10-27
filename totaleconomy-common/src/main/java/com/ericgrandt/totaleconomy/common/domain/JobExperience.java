package com.ericgrandt.totaleconomy.common.domain;

public class JobExperience {
    private final String id;
    private final String accountId;
    private final String jobId;
    private int experience;

    public JobExperience(
        final String id,
        final String accountId,
        final String jobId,
        final int experience
    ) {
        this.id = id;
        this.accountId = accountId;
        this.jobId = jobId;
        this.experience = experience;
    }

    public int getLevel() {
        int level = (int) Math.ceil(Math.sqrt(experience) / 7);
        return Math.max(level, 1);
    }

    public int getCurrentLevelBaseExperience() {
        int experience = (int) Math.ceil(49 * Math.pow(getLevel() - 1, 2));
        return experience > 0 ? experience + 1 : experience;
    }

    public int getNextLevelExperience() {
        return (int) Math.ceil(49 * Math.pow(getLevel(), 2)) + 1;
    }

    public boolean addExperience(int experienceToAdd) {
        boolean willLevelUp = (experience + experienceToAdd) >= getNextLevelExperience();
        experience += experienceToAdd;
        return willLevelUp;
    }

    public String getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public int getExperience() {
        return experience;
    }
}

