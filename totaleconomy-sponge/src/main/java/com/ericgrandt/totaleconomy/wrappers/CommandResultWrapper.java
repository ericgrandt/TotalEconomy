package com.ericgrandt.totaleconomy.wrappers;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;

public class CommandResultWrapper {
    public CommandResult success() {
        return CommandResult.success();
    }

    public CommandResult error(Component errorMessage) {
        return CommandResult.error(errorMessage);
    }
}
