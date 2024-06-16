package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.listeners.CommonPlayerListener;
import com.ericgrandt.totaleconomy.commonimpl.BukkitPlayer;
import java.util.concurrent.CompletableFuture;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final CommonPlayerListener commonPlayerListener;

    public PlayerListener(
        final CommonPlayerListener commonPlayerListener
    ) {
        this.commonPlayerListener = commonPlayerListener;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> commonPlayerListener.onPlayerJoin(
            new BukkitPlayer(event.getPlayer()))
        );
    }
}
