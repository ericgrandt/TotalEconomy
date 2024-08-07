package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.CommonParameter;
import com.ericgrandt.totaleconomy.common.command.PayCommand;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import java.math.BigDecimal;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class PayCommandExecutor implements CommandExecutor {
    private final PayCommand payCommand;
    private final SpongeWrapper spongeWrapper;

    public PayCommandExecutor(final PayCommand payCommand, final SpongeWrapper spongeWrapper) {
        this.payCommand = payCommand;
        this.spongeWrapper = spongeWrapper;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        if (!(context.cause().root() instanceof ServerPlayer player)) {
            return spongeWrapper.error(Component.text("This command can only be used by a player"));
        }

        Parameter.Value<ServerPlayer> toPlayerParameter = spongeWrapper.playerParameter("toPlayer");
        Parameter.Value<Double> amountParameter = spongeWrapper.doubleParameter("amount");

        ServerPlayer toPlayer = context.requireOne(toPlayerParameter);
        BigDecimal amount = BigDecimal.valueOf(context.requireOne(amountParameter));

        Map<String, CommonParameter<?>> argsMap = Map.of(
            "toPlayer", new CommonParameter<>(new SpongePlayer(toPlayer)),
            "amount", new CommonParameter<>(amount)
        );

        payCommand.execute(new SpongePlayer(player), argsMap);

        return spongeWrapper.success();
    }
}
