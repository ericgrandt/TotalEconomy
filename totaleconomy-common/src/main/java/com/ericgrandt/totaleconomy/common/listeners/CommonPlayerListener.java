package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.jobs.ExperienceBar;
import com.ericgrandt.totaleconomy.common.models.CreateJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.services.JobService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class CommonPlayerListener {
    private final CommonEconomy economy;
    private final Optional<JobService> jobServiceOpt;

    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);

    public CommonPlayerListener(final CommonEconomy economy, final Optional<JobService> jobServiceOpt) {
        this.economy = economy;
        this.jobServiceOpt = jobServiceOpt;

        scheduler.setRemoveOnCancelPolicy(true);
    }

    public void onPlayerJoin(CommonPlayer player) {
        UUID uuid = player.getUniqueId();

        economy.createAccount(uuid);

        if (jobServiceOpt.isPresent()) {
            JobService jobService = jobServiceOpt.get();
            jobService.createJobExperience(new CreateJobExperienceRequest(uuid));
            jobService.addPlayerExperienceBar(uuid, new ExperienceBar(player, scheduler));
        }
    }
}
