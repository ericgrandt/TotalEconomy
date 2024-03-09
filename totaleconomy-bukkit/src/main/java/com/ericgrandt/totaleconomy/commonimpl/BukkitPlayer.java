package com.ericgrandt.totaleconomy.commonimpl;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public record BukkitPlayer(Player player) implements CommonPlayer {
    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public void sendMessage(Component message) {
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
}
