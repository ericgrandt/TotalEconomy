package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.jobs.ExperienceBar;
import com.ericgrandt.totaleconomy.common.models.CreateJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.services.JobService;
import java.util.UUID;

public class CommonPlayerListener {
    private final CommonEconomy economy;
    private final JobService jobService;

    public CommonPlayerListener(final CommonEconomy economy, final JobService jobService) {
        this.economy = economy;
        this.jobService = jobService;
    }

    public void onPlayerJoin(CommonPlayer player) {
        UUID uuid = player.getUniqueId();

        economy.createAccount(uuid);
        // TODO: Test
        jobService.createJobExperience(new CreateJobExperienceRequest(uuid));
        jobService.addPlayerExperienceBar(uuid, new ExperienceBar(player));
    }
}
