package com.ericgrandt.totaleconomy.jobs.paper.listener;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.jobs.paper.mapper.ExceptionMapper;
import com.ericgrandt.totaleconomy.jobs.service.BlockBreakService;
import com.ericgrandt.totaleconomy.jobs.service.JobService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final AsyncTaskRunner taskRunner;
    private final ExceptionMapper exceptionMapper;
    private final BlockBreakService blockBreakService;
    private final JobService jobService;

    public BlockBreakListener(
        AsyncTaskRunner taskRunner,
        ExceptionMapper exceptionMapper,
        BlockBreakService blockBreakService,
        JobService jobService
    ) {
        this.taskRunner = taskRunner;
        this.exceptionMapper = exceptionMapper;
        this.blockBreakService = blockBreakService;
        this.jobService = jobService;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        var activeJob = jobService.getActiveJob(player.getUniqueId());
        var blockName = event.getBlock().getState().getBlock().getType().name();

        // TODO: Maybe before executing runAsync, we do a quick action + block existence check against
        //  the config file instead of waiting until we call handleAction. That should be safe to run
        //  in the same thread?
        taskRunner.runAsync(() -> {
            try {
                blockBreakService.handleAction(player.getUniqueId(), activeJob.jobId(), blockName);
            } catch (Exception e) {
                player.sendMessage(exceptionMapper.handleException(e));
            }
        });
    }
}
