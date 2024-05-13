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

    public int level() {
        int level = (int) Math.ceil(Math.sqrt(experience) / 7);
        return Math.max(level, 1);
    }

    public int nextLevelExperience() {
        return (int) Math.ceil(49 * Math.pow(level(), 2)) + 1;
    }

    public boolean addExperience(int experienceToAdd) {
        boolean willLevelUp = (experience + experienceToAdd) >= nextLevelExperience();
        experience += experienceToAdd;
        return willLevelUp;
    }

    public String getId() {
        return id;
    }

    public int getExperience() {
        return experience;
    }
}

