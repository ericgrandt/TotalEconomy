package com.ericgrandt.totaleconomy.common.domain;

public class JobExperience {
    private final String id;
    private final String accountId;
    private final String jobId;
    private final int experience;

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

    public boolean willLevelUp(int experienceToAdd) {
        return false;
    }
//
//
//    public int currentLevelBaseExperience() {
//        int experience = (int) Math.ceil(49 * Math.pow(level() - 1, 2));
//        return experience > 0 ? experience + 1 : experience;
//    }
}

