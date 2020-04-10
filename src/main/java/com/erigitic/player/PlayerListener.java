package com.erigitic.player;

import com.erigitic.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerListener {
    private final TotalEconomy plugin;
    private final Logger logger;

    public PlayerListener() {
        plugin = TotalEconomy.getPlugin();
        logger = plugin.getLogger();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

        logger.info(player.getName());
    }
}
