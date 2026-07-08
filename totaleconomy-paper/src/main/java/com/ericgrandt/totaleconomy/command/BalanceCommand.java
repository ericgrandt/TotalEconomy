package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.mapper.CommandExceptionMapper;
import com.ericgrandt.totaleconomy.service.EconomyService;
import com.ericgrandt.totaleconomy.util.AsyncTaskRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements CommandExecutor {
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

    @Override
    public boolean onCommand(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String label,
        @NotNull String @NotNull [] args
    ) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        var hasCurrencyCode = args.length > 0;

        taskRunner.runAsync(
            plugin, () -> {
                try {
                    var balanceResult = hasCurrencyCode ?
                        economyService.getAccountBalance(
                            player.getUniqueId(),
                            args[0]
                        ) : economyService.getAccountBalance(player.getUniqueId());
                    var formattedBalance = balanceResult.currency().format(balanceResult.balance());

                    player.sendMessage(Messages.balance(formattedBalance));
                } catch (Exception e) {
                    player.sendMessage(exceptionMapper.handleException(e));
                }
            }
        );

        return true;
    }
}
