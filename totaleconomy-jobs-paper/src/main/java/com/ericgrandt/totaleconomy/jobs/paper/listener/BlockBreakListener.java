package com.ericgrandt.totaleconomy.jobs.paper.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        player.sendMessage(event.getBlock().getState().getBlock().getType().name());
    }
}
