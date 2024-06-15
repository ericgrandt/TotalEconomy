package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.TotalEconomy;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.models.CreateJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.services.JobService;
import com.ericgrandt.totaleconomy.commonimpl.BukkitPlayer;
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

    // TODO: Use CommonPlayerJoinHandler
    private void onPlayerJoinHandler(Player player) {
        UUID uuid = player.getUniqueId();

        jobService.addJobExperienceBar(new BukkitPlayer(player));
        economy.createAccount(uuid);

        CreateJobExperienceRequest createJobExperienceRequest = new CreateJobExperienceRequest(uuid);
        jobService.createJobExperience(createJobExperienceRequest);
    }

    // TODO: Player leave handler to remove job experience bar from job service
    // TODO: Use CommonPlayerLeaveHandler
}
