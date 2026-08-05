package com.ericgrandt.totaleconomy.paper.listener;

import com.ericgrandt.totaleconomy.api.infra.AsyncTaskRunner;
import com.ericgrandt.totaleconomy.api.service.EconomyService;
import com.ericgrandt.totaleconomy.model.TECurrency;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;

public class JoinListener implements Listener {
    private final AsyncTaskRunner taskRunner;
    private final Logger logger;
    private final EconomyService<TECurrency> economyService;

    public JoinListener(
        AsyncTaskRunner taskRunner,
        Logger logger,
        EconomyService<TECurrency> economyService
    ) {
        this.taskRunner = taskRunner;
        this.logger = logger;
        this.economyService = economyService;
    }

    // TODO: Test
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        taskRunner.runAsync(
            () -> {
                try {
                    for (var entry : economyService.getSupportedCurrencies().entrySet()) {
                        economyService.createAccount(player.getUniqueId(), entry.getKey());
                    }
                } catch (Exception e) {
                    logger.error("failed to create account on player join", e);
                }
            }
        );
    }
}
