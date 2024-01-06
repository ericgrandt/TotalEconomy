package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.TotalEconomy;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.impl.JobExperienceBar;
import com.ericgrandt.totaleconomy.services.JobService;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final EconomyImpl economy;
    private final JobService jobService;
    private final TotalEconomy plugin;

    public PlayerListener(EconomyImpl economy, JobService jobService, TotalEconomy plugin) {
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
        jobService.addPlayerJobExperienceBar(player.getUniqueId(), new JobExperienceBar(player, plugin));

        if (!economy.hasAccount(player)) {
            economy.createPlayerAccount(player);
        }

        jobService.createJobExperienceForAccount(player.getUniqueId());
    }
}
