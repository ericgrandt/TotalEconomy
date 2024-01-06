package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.impl.JobExperienceBar;
import com.ericgrandt.totaleconomy.models.AddExperienceResult;
import com.ericgrandt.totaleconomy.models.JobExperience;
import com.ericgrandt.totaleconomy.services.JobService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class JobListener implements Listener {
    private final EconomyImpl economy;
    private final JobService jobService;

    public JobListener(EconomyImpl economy, JobService jobService) {
        this.economy = economy;
        this.jobService = jobService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakAction(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String blockName = event.getBlock().getType().name().toLowerCase();
        JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());

        if (event.getBlock().getBlockData() instanceof Ageable age && age.getAge() != age.getMaximumAge()) {
            return;
        }

        CompletableFuture.runAsync(() -> actionHandler(blockName, player, "break", jobExperienceBar));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKillAction(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (player == null) {
            return;
        }

        String entityName = entity.getType().name().toLowerCase();
        JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());;

        CompletableFuture.runAsync(() -> actionHandler(entityName, player, "kill", jobExperienceBar));
    }

    @EventHandler(ignoreCancelled = true)
    public void onFishAction(PlayerFishEvent event) {
        Entity caughtEntity = event.getCaught();
        if (caughtEntity == null) {
            return;
        }

        Player player = event.getPlayer();

        String caughtItemName = ((Item) caughtEntity).getItemStack().getType().name().toLowerCase();
        JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());;

        CompletableFuture.runAsync(() -> actionHandler(caughtItemName, player, "fish", jobExperienceBar));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceAction(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String blockName = event.getBlock().getType().name().toLowerCase();
        JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());

        CompletableFuture.runAsync(() -> actionHandler(blockName, player, "place", jobExperienceBar));
    }

    public void actionHandler(String materialName, Player player, String action, JobExperienceBar jobExperienceBar) {
        JobRewardDto jobRewardDto = jobService.getJobReward(action, materialName);
        if (jobRewardDto == null) {
            return;
        }

        addExperience(player, jobRewardDto, jobExperienceBar);
        economy.depositPlayer(player, jobRewardDto.money().doubleValue());
    }

    private void addExperience(Player player, JobRewardDto jobRewardDto, JobExperienceBar jobExperienceBar) {
        int experienceGain = jobRewardDto.experience();

        AddExperienceResult addExperienceResult = jobService.addExperience(
            player.getUniqueId(),
            UUID.fromString(jobRewardDto.jobId()),
            experienceGain
        );
        if (addExperienceResult.leveledUp()) {
            player.sendMessage(getLevelUpMessage(addExperienceResult));
        }

        JobExperience jobExperience = addExperienceResult.jobExperience();
        jobExperienceBar.setExperienceBarName(jobExperience, experienceGain);
        jobExperienceBar.setProgress(jobExperience);
        jobExperienceBar.show();
    }

    private Component getLevelUpMessage(AddExperienceResult addExperienceResult) {
        return Component.text(
            addExperienceResult.jobExperience().jobName(),
            TextColor.fromHexString("#DADFE1"),
            TextDecoration.BOLD
        ).append(
            Component.text(
                " is now level",
                TextColor.fromHexString("#708090")
            ).decoration(TextDecoration.BOLD, false)
        ).append(
            Component.text(
                String.format(" %s", addExperienceResult.jobExperience().level()),
                TextColor.fromHexString("#DADFE1"),
                TextDecoration.BOLD
            )
        );
    }
}
