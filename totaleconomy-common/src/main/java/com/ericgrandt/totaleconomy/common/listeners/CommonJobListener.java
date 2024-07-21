package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import com.ericgrandt.totaleconomy.common.services.JobService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// TODO: Add support for BossBar experience tracking
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
        CommonPlayer player = event.player();

        CompletableFuture.runAsync(() -> {
            GetJobRewardResponse jobRewardResponse = jobService.getJobReward(
                new GetJobRewardRequest(event.action(), event.material())
            );

            AddExperienceRequest addExperienceRequest = new AddExperienceRequest(
                player.getUniqueId(),
                UUID.fromString(jobRewardResponse.jobId()),
                jobRewardResponse.experience()
            );
            jobService.addExperience(addExperienceRequest);
            economy.deposit(player.getUniqueId(), currencyId, jobRewardResponse.money(), false);
        });
    }
}
