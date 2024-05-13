package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.services.JobService;
import java.util.concurrent.CompletableFuture;

public class CommonJobListener {
    private final CommonEconomy economy;
    private final JobService jobService;
    private final int currencyId;

    public CommonJobListener(
        final CommonEconomy economy,
        final JobService jobService,
        final int currencyId
    ) {
        this.economy = economy;
        this.jobService = jobService;
        this.currencyId = currencyId;
    }

    public void handleAction(JobEvent event) {
        CompletableFuture.runAsync(() -> {
            // AddExperienceRequest(accountId, jobId, action, materialName);
            // jobService.addExperience(addExperienceRequest);
        });
        // Get job action reward (JobActionReward domain object) or do we
        //  handle grabbing this in the service?
        // Check for existence of job action reward
        // Deposit money
        // Add experience to job (Job w/ JobExperience domain object)
    }
}
