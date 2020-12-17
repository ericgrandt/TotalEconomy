package com.erigitic.player;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEEconomyService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
public class PlayerListener {
    private final TEEconomyService economyService;

    public PlayerListener() {
        TotalEconomy plugin = TotalEconomy.getPlugin();
        economyService = plugin.getEconomyService();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

        economyService.getOrCreateAccount(player.getUniqueId());
    }
}