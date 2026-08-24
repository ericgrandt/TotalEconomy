package com.ericgrandt.totaleconomy.jobs.paper.listener;

import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import com.ericgrandt.totaleconomy.jobs.job.RewardCalculator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final RewardCalculator rewardCalculator;

    // TODO: This should take in BlockBreakService, not RewardCalculator
    public BlockBreakListener(RewardCalculator rewardCalculator) {
        this.rewardCalculator = rewardCalculator;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        var blockName = event.getBlock().getState().getBlock().getType().name();
        var entryOpt = rewardCalculator.getEntry("miner", JobEnums.ActionType.BLOCK_BREAK, blockName);
        if (entryOpt.isEmpty()) {
            return;
        }

        player.sendMessage(String.valueOf(entryOpt.get().xp()));
        //player.sendMessage(entryOpt.orElseGet(null));
    }
}
