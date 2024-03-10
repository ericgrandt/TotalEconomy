package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerListener {
    private final CommonEconomy economy;

    public PlayerListener(final CommonEconomy economy) {
        this.economy = economy;
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        ServerPlayer player = event.player();
        UUID uuid = player.uniqueId();

        CompletableFuture.runAsync(() -> economy.createAccount(uuid));
    }
}
