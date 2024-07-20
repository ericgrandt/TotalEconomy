package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.BalanceCommand;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class BalanceCommandExecutor implements CommandExecutor {
    private final BalanceCommand balanceCommand;
    private final SpongeWrapper spongeWrapper;

    public BalanceCommandExecutor(final BalanceCommand balanceCommand, final SpongeWrapper spongeWrapper) {
        this.balanceCommand = balanceCommand;
        this.spongeWrapper = spongeWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (!(context.cause().root() instanceof ServerPlayer player)) {
            return spongeWrapper.error(Component.text("This command can only be used by a player"));
        }

        balanceCommand.execute(new SpongePlayer(player), null);

        return spongeWrapper.success();
    }
}
