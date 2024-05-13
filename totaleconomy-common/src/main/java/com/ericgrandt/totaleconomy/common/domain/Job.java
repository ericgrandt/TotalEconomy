package com.ericgrandt.totaleconomy.common.domain;

public class Job {
    private final String id;
    private final String jobName;

    private JobExperience jobExperience;

    public Job(final String id, final String jobName) {
        this.id = id;
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public JobExperience getJobExperience() {
        return jobExperience;
    }

    public void setJobExperience(JobExperience jobExperience) {
        this.jobExperience = jobExperience;
    }
}
