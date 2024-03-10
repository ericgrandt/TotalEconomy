package com.ericgrandt.totaleconomy.commonimpl;

import com.ericgrandt.totaleconomy.common.game.CommonPlayer;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public record SpongePlayer(ServerPlayer player) implements CommonPlayer {
    @Override
    public void sendMessage(Component message) {
        player.sendMessage(message);
    }

    @Override
    public UUID getUniqueId() {
        return player.uniqueId();
    }

    @Override
    public String getName() {
        return player.name();
    }

    @Override
    public boolean isNull() {
        return player == null;
    }
}
