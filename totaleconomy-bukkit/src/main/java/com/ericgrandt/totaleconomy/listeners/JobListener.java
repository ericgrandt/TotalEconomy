package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import com.ericgrandt.totaleconomy.commonimpl.BukkitPlayer;
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
    private final CommonJobListener commonJobListener;

    public JobListener(final CommonJobListener commonJobListener) {
        this.commonJobListener = commonJobListener;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakAction(BlockBreakEvent event) {
        BukkitPlayer player = new BukkitPlayer(event.getPlayer());
        String blockName = event.getBlock().getType().name().toLowerCase();
        //JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());

        if (event.getBlock().getBlockData() instanceof Ageable age && age.getAge() != age.getMaximumAge()) {
            return;
        }

        commonJobListener.handleAction(new JobEvent(player, "break", blockName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKillAction(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        BukkitPlayer player = new BukkitPlayer(entity.getKiller());
        if (player.isNull()) {
            return;
        }

        String entityName = entity.getType().name().toLowerCase();
        //JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());

        commonJobListener.handleAction(new JobEvent(player, "kill", entityName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onFishAction(PlayerFishEvent event) {
        Entity caughtEntity = event.getCaught();
        if (caughtEntity == null) {
            return;
        }

        BukkitPlayer player = new BukkitPlayer(event.getPlayer());

        String caughtItemName = ((Item) caughtEntity).getItemStack().getType().name().toLowerCase();
        //JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());;

        commonJobListener.handleAction(new JobEvent(player, "fish", caughtItemName));
    }

//    @EventHandler(ignoreCancelled = true)
//    public void onPlaceAction(BlockPlaceEvent event) {
//        Player player = event.getPlayer();
//        String blockName = event.getBlock().getType().name().toLowerCase();
//        JobExperienceBar jobExperienceBar = jobService.getPlayerJobExperienceBar(player.getUniqueId());
//
//        CompletableFuture.runAsync(() -> actionHandler(blockName, player, "place", jobExperienceBar));
//    }
}
