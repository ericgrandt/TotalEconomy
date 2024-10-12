package com.ericgrandt.totaleconomy.commonimpl;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import java.util.UUID;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record BukkitPlayer(Player player) implements CommonPlayer {
    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(message);
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean isNull() {
        return player == null;
    }

    @Override
    public void showBossBar(BossBar bossBar) {
        player.showBossBar(bossBar);
    }

    @Override
    public void hideBossBar(BossBar bossBar) {
        player.hideBossBar(bossBar);
    }
}
