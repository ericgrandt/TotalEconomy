package com.ericgrandt.totaleconomy.common.domain;

import java.math.BigDecimal;

public class JobReward {
    private final String id;
    private final String jobId;
    private final String jobActionId;
    private final int currencyId;
    private final String material;
    private final BigDecimal money;
    private final int experience;

    public JobReward(
        final String id,
        final String jobId,
        final String jobActionId,
        final int currencyId,
        final String material,
        final BigDecimal money,
        final int experience
    ) {
        this.id = id;
        this.jobId = jobId;
        this.jobActionId = jobActionId;
        this.currencyId = currencyId;
        this.material = material;
        this.money = money;
        this.experience = experience;
    }

    public String getJobId() {
        return jobId;
    }

    public int getExperience() {
        return experience;
    }
}
