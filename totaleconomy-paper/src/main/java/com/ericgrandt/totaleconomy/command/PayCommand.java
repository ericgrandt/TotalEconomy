package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.TEEconomyService;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public class PayCommand {
    private final Plugin plugin;
    private final AsyncTaskRunner taskRunner;
    private final CommandExceptionMapper exceptionMapper;
    private final TEEconomyService economyService;

    public PayCommand(
        Plugin plugin,
        AsyncTaskRunner taskRunner,
        CommandExceptionMapper exceptionMapper,
        TEEconomyService economyService
    ) {
        this.plugin = plugin;
        this.taskRunner = taskRunner;
        this.exceptionMapper = exceptionMapper;
        this.economyService = economyService;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("pay")
            .requires(source -> source.getSender() instanceof Player)
            .then(Commands.argument("toPlayer", ArgumentTypes.player())
                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
                    .then(Commands.argument("currency", StringArgumentType.string())
                        .executes(this::executeWithCurrency)
                    )
                    .executes(this::executeWithDefault)))
            .build();
    }

    // TODO: These two execute functions need to be deduped
    int executeWithCurrency(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var fromPlayer = (Player) ctx.getSource().getSender();
        var toPlayer = ctx.getArgument("toPlayer", PlayerSelectorArgumentResolver.class)
            .resolve(ctx.getSource())
            .getFirst();
        var amount = BigDecimal.valueOf(ctx.getArgument("amount", Double.class));
        var currency = ctx.getArgument("currency", String.class);

        taskRunner.runAsync(
            plugin, () -> {
                try {
                    var transferResult = economyService.transfer(
                        fromPlayer.getUniqueId(),
                        toPlayer.getUniqueId(),
                        currency,
                        amount
                    );
                    var formattedBalance = transferResult.currency().format(transferResult.amount());

                    fromPlayer.sendMessage(Messages.payFrom(formattedBalance, toPlayer.getName()));
                    toPlayer.sendMessage(Messages.payTo(formattedBalance, fromPlayer.getName()));
                } catch (Exception e) {
                    fromPlayer.sendMessage(exceptionMapper.handleException(e));
                }
            }
        );

        return Command.SINGLE_SUCCESS;
    }

    int executeWithDefault(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var fromPlayer = (Player) ctx.getSource().getSender();
        var toPlayer = ctx.getArgument("toPlayer", PlayerSelectorArgumentResolver.class)
            .resolve(ctx.getSource())
            .getFirst();
        var amountDouble = ctx.getArgument("amount", Double.class);
        var amount = BigDecimal.valueOf(amountDouble);

        taskRunner.runAsync(
            plugin, () -> {
                try {
                    var transferResult = economyService.transfer(
                        fromPlayer.getUniqueId(),
                        toPlayer.getUniqueId(),
                        amount
                    );
                    var formattedBalance = transferResult.currency().format(transferResult.amount());

                    fromPlayer.sendMessage(Messages.payFrom(formattedBalance, toPlayer.getName()));
                    toPlayer.sendMessage(Messages.payTo(formattedBalance, fromPlayer.getName()));
                } catch (Exception e) {
                    fromPlayer.sendMessage(exceptionMapper.handleException(e));
                }
            }
        );

        return Command.SINGLE_SUCCESS;
    }
}
