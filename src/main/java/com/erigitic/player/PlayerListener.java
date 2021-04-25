package com.erigitic.player;

import com.erigitic.TotalEconomy;
import com.erigitic.services.TEEconomyService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerListener {
    private final TEEconomyService economyService;

    public PlayerListener() {
        TotalEconomy plugin = TotalEconomy.getPlugin();
        economyService = plugin.getEconomyService();
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        Player player = event.player();

        economyService.findOrCreateAccount(player.uniqueId());
    }
}