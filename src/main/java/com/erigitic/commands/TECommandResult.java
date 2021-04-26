package com.erigitic.commands;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;

import java.util.Optional;

public class TECommandResult implements CommandResult {
    private final boolean isSuccess;

    public TECommandResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public int result() {
        return 0;
    }

    @Override
    public Optional<Component> errorMessage() {
        return Optional.empty();
    }
}
