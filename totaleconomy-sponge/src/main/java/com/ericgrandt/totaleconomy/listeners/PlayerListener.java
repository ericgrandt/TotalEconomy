package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.listeners.CommonPlayerListener;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerListener {
    private final CommonPlayerListener commonPlayerListener;

    public PlayerListener(
        final CommonPlayerListener commonPlayerListener
    ) {
        this.commonPlayerListener = commonPlayerListener;
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        CompletableFuture.runAsync(() -> commonPlayerListener.onPlayerJoin(
            new SpongePlayer(event.player()))
        );
    }
}
