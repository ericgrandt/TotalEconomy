package com.ericgrandt.totaleconomy.listener;

import com.ericgrandt.totaleconomy.service.EconomyService;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

public class JoinListener implements Listener {
    private final Plugin plugin;
    private final AsyncTaskRunner taskRunner;
    private final Logger logger;
    private final EconomyService economyService;

    public JoinListener(
        Plugin plugin,
        AsyncTaskRunner taskRunner,
        Logger logger,
        EconomyService economyService
    ) {
        this.plugin = plugin;
        this.taskRunner = taskRunner;
        this.logger = logger;
        this.economyService = economyService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        taskRunner.runAsync(
            plugin, () -> {
                try {
                    var defaultCurrency = economyService.getDefaultCurrency();
                    economyService.createAccount(player.getUniqueId(), defaultCurrency.code());
                } catch (Exception e) {
                    logger.error("failed to create an account on player join", e);
                }
            }
        );
    }
}
