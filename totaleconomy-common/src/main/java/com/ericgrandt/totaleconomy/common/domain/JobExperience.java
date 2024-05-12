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
}

