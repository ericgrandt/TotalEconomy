package com.ericgrandt.totaleconomy.wrappers;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;
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
}
