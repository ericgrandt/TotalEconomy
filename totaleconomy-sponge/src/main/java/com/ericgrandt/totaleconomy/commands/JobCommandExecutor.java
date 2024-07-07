package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.JobCommand;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

// TODO: Test
public class JobCommandExecutor implements CommandExecutor {
    private final JobCommand jobCommand;
    private final SpongeWrapper spongeWrapper;

    public JobCommandExecutor(final JobCommand jobCommand, final SpongeWrapper spongeWrapper) {
        this.jobCommand = jobCommand;
        this.spongeWrapper = spongeWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (!(context.cause().root() instanceof ServerPlayer player)) {
            return spongeWrapper.error(Component.text("This command can only be used by a player"));
        }

        jobCommand.execute(new SpongePlayer(player), null);

        return spongeWrapper.success();
    }
}
