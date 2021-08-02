package com.ericgrandt.domain;

import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandResult;

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
        return isSuccess ? 0 : 1;
    }

    @Override
    public Optional<Component> errorMessage() {
        throw new UnsupportedOperationException();
    }
}
