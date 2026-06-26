package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.model.TEPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PaperPlayer implements TEPlayer {
    private final Player player;

    public PaperPlayer(Player player) {
        this.player = player;
    }

    @Override
    public UUID uniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String name() {
        return player.getName();
    }

    @Override
    public void sendMessage(Component message) {
        player.sendMessage(message);
    }
}
