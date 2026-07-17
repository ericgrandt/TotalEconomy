package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.EconomyService;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BalanceCommand {
    private final Plugin plugin;
    private final AsyncTaskRunner taskRunner;
    private final CommandExceptionMapper exceptionMapper;
    private final EconomyService economyService;

    public BalanceCommand(
        Plugin plugin,
        AsyncTaskRunner taskRunner,
        CommandExceptionMapper exceptionMapper,
        EconomyService economyService
    ) {
        this.plugin = plugin;
        this.taskRunner = taskRunner;
        this.exceptionMapper = exceptionMapper;
        this.economyService = economyService;
    }

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("balance")
            .requires(source -> source.getSender() instanceof Player)
            .then(Commands.argument("currency", StringArgumentType.string())
                //.suggests(this::listCurrencies) // TODO: Add this eventually
                .executes(this::executeWithCurrency)
            ).executes(this::executeWithDefault)
            .build();
    }

    // TODO: These two execute functions need to be deduped
    int executeWithCurrency(CommandContext<CommandSourceStack> ctx) {
        var player = (Player) ctx.getSource().getSender();
        var currencyCode = ctx.getArgument("currency", String.class);

        taskRunner.runAsync(
            plugin, () -> {
                try {
                    var balanceResult = economyService.getAccountBalance(player.getUniqueId(), currencyCode);
                    var formattedBalance = balanceResult.currency().format(balanceResult.balance());

                    player.sendMessage(Messages.balance(formattedBalance));
                } catch (Exception e) {
                    player.sendMessage(exceptionMapper.handleException(e));
                }
            }
        );

        return Command.SINGLE_SUCCESS;
    }

    int executeWithDefault(CommandContext<CommandSourceStack> ctx) {
        var player = (Player) ctx.getSource().getSender();

        taskRunner.runAsync(
            plugin, () -> {
                try {
                    var balanceResult = economyService.getAccountBalance(player.getUniqueId());
                    var formattedBalance = balanceResult.currency().format(balanceResult.balance());

                    player.sendMessage(Messages.balance(formattedBalance));
                } catch (Exception e) {
                    player.sendMessage(exceptionMapper.handleException(e));
                }
            }
        );

        return Command.SINGLE_SUCCESS;
    }
}
