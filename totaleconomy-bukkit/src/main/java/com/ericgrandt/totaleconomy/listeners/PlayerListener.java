package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.TotalEconomy;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.impl.JobExperienceBar;
import com.ericgrandt.totaleconomy.services.JobService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final CommonEconomy economy;
    private final JobService jobService;
    private final TotalEconomy plugin;

    public PlayerListener(
        final CommonEconomy economy,
        final JobService jobService,
        final TotalEconomy plugin
    ) {
        this.economy = economy;
        this.jobService = jobService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CompletableFuture.runAsync(() -> onPlayerJoinHandler(player));
    }

    public void onPlayerJoinHandler(Player player) {
        UUID uuid = player.getUniqueId();

        jobService.addPlayerJobExperienceBar(uuid, new JobExperienceBar(player, plugin));
        economy.createAccount(uuid);
        jobService.createJobExperienceForAccount(uuid);
    }
}
