package com.erigitic.player;

import com.erigitic.TotalEconomy;
import com.erigitic.economy.TEEconomyService;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerListener {
    private final TotalEconomy plugin;
    private final Logger logger;
    private TEEconomyService economyService;

    public PlayerListener() {
        plugin = TotalEconomy.getPlugin();
        logger = plugin.getLogger();
        economyService = plugin.getEconomyService();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

        economyService.getOrCreateAccount(player.getUniqueId());
    }
}
