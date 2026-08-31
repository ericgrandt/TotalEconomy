package com.ericgrandt.totaleconomy.jobs.paper.listener;

import com.ericgrandt.totaleconomy.jobs.dto.Status;
import com.ericgrandt.totaleconomy.jobs.service.BlockBreakService;
import com.ericgrandt.totaleconomy.jobs.service.JobService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final BlockBreakService blockBreakService;
    private final JobService jobService;

    public BlockBreakListener(BlockBreakService blockBreakService, JobService jobService) {
        this.blockBreakService = blockBreakService;
        this.jobService = jobService;
    }

    // TODO: Test
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        var activeJob = jobService.getActiveJob(player.getUniqueId());
        var blockName = event.getBlock().getState().getBlock().getType().name();

        var handleResult = blockBreakService.handleAction(player.getUniqueId(), activeJob.jobId(), blockName);
        if (handleResult.status() == Status.NOENTRY) {
            return;
        }

        //player.sendMessage(String.valueOf(entryOpt.get().xp()));
        //player.sendMessage(entryOpt.orElseGet(null));
    }
}
