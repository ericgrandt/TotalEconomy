package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.AddExperienceResponse;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import com.ericgrandt.totaleconomy.common.services.JobService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

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
            GetJobRewardResponse jobRewardResponse = jobService.getJobReward(
                new GetJobRewardRequest(event.action(), event.material())
            );
            AddExperienceRequest addExperienceRequest = new AddExperienceRequest(
                event.player().getUniqueId(),
                UUID.fromString(jobRewardResponse.jobId()),
                jobRewardResponse.experience()
            );
            AddExperienceResponse addExperienceResponse = jobService.addExperience(addExperienceRequest);
            if (addExperienceResponse.leveledUp()) {
                sendLevelUpMessage(event.player(), addExperienceResponse);
            }

            economy.deposit(event.player().getUniqueId(), currencyId, jobRewardResponse.money(), false);
        });
    }

    private void sendLevelUpMessage(CommonPlayer player, AddExperienceResponse addExperienceResponse) {
        player.sendMessage(
            Component.text(
                addExperienceResponse.jobName(),
                TextColor.fromHexString("#DADFE1"),
                TextDecoration.BOLD
            ).append(
                Component.text(
                    " is now level",
                    TextColor.fromHexString("#708090")
                ).decoration(TextDecoration.BOLD, false)
            ).append(
                Component.text(
                    String.format(" %s", addExperienceResponse.level()),
                    TextColor.fromHexString("#DADFE1"),
                    TextDecoration.BOLD
                )
            )
        );
    }
}
