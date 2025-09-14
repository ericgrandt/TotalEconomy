package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.dto.ExperienceBarDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.jobs.ExperienceBar;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import com.ericgrandt.totaleconomy.common.services.JobService;
import java.util.UUID;
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
        CommonPlayer player = event.player();
        ExperienceBar experienceBar = jobService.getPlayerExperienceBar(player.getUniqueId());

        CompletableFuture.runAsync(() -> {
            GetJobRewardResponse jobRewardResponse = jobService.getJobReward(
                new GetJobRewardRequest(event.action(), event.material())
            );

            addExperience(player, jobRewardResponse);
            showExperienceBar(player, jobRewardResponse, experienceBar);
            economy.deposit(player.getUniqueId(), currencyId, jobRewardResponse.money(), false);
        });
    }

    private void addExperience(CommonPlayer player, GetJobRewardResponse jobRewardResponse) {
        AddExperienceRequest addExperienceRequest = new AddExperienceRequest(
            player.getUniqueId(),
            UUID.fromString(jobRewardResponse.jobId()),
            jobRewardResponse.experience()
        );
        jobService.addExperience(addExperienceRequest);
    }

    private void showExperienceBar(CommonPlayer player, GetJobRewardResponse jobRewardResponse, ExperienceBar experienceBar) {
        if (experienceBar != null) {
            GetJobExperienceRequest jobExperienceRequest = new GetJobExperienceRequest(
                player.getUniqueId(),
                UUID.fromString(jobRewardResponse.jobId())
            );
            ExperienceBarDto experienceBarDto = jobService.getExperienceBarDto(jobExperienceRequest);
            experienceBar.show(experienceBarDto, jobRewardResponse.experience());
        }
    }
}
