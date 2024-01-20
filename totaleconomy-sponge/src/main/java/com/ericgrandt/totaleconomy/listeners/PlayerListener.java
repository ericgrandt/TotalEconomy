package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerListener {
    private final EconomyImpl economy;

    public PlayerListener(EconomyImpl economy) {
        this.economy = economy;
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        ServerPlayer player = event.player();

        CompletableFuture.runAsync(() -> economy.findOrCreateAccount(player.uniqueId()));
    }
}
