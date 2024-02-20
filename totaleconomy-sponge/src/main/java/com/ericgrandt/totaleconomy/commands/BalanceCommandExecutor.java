package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.BalanceCommand;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.wrappers.CommandResultWrapper;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class BalanceCommandExecutor implements CommandExecutor {
    private final EconomyImpl economy;
    private final CommandResultWrapper commandResultWrapper;

    public BalanceCommandExecutor(final EconomyImpl economy, final CommandResultWrapper commandResultWrapper) {
        this.economy = economy;
        this.commandResultWrapper = commandResultWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (!(context.cause().root() instanceof ServerPlayer player)) {
            return CommandResult.error(Component.text("This command can only be used by a player"));
        }

        new BalanceCommand(economy).execute(new SpongePlayer(player), null);

        return commandResultWrapper.success();
    }
}
