package com.ericgrandt.totaleconomy.wrappers;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

public class SpongeWrapper {
    public CommandResult success() {
        return CommandResult.success();
    }

    public CommandResult error(Component errorMessage) {
        return CommandResult.error(errorMessage);
    }

    public TransactionType deposit() {
        return TransactionTypes.DEPOSIT.get();
    }

    public TransactionType withdraw() {
        return TransactionTypes.WITHDRAW.get();
    }

    public TransactionType transfer() {
        return TransactionTypes.TRANSFER.get();
    }

    public Parameter.Value<Double> doubleParameter(String key) {
        return Parameter.doubleNumber().key(key).build();
    }

    public Parameter.Value<ServerPlayer> playerParameter(String key) {
        return Parameter.player().key(key).build();
    }
}
