package com.ericgrandt.totaleconomy.common.listeners;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.services.JobService;

public class CommonPlayerListener {
    private final JobService jobService;

    public CommonPlayerListener(final JobService jobService) {
        this.jobService = jobService;
    }

    public void onPlayerJoin(CommonPlayer player) {
    }

    public void onPlayerLeave(CommonPlayer player) {
    }
}
