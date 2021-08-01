package com.ericgrandt.commands.models;

import org.spongepowered.api.entity.living.player.Player;

public class PayCommandPlayers {
    private final Player fromPlayer;
    private final Player toPlayer;

    public PayCommandPlayers(Player fromPlayer, Player toPlayer) {
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
    }

    public Player getFromPlayer() {
        return fromPlayer;
    }

    public Player getToPlayer() {
        return toPlayer;
    }
}
