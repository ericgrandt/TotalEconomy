package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.impl.PaperPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BalanceCommandExecutor implements CommandExecutor {
    private final Plugin plugin;
    private final BalanceCommand balanceCommand;

    public BalanceCommandExecutor(Plugin plugin, BalanceCommand balanceCommand) {
        this.plugin = plugin;
        this.balanceCommand = balanceCommand;
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

        // TODO: Think about creating a CommandArgument.builder() that will handle type checking and everything. Maybe
        //  handle optional and required args? Or keep it simple and have each executor fail early if not all args are
        //  provided.
        Map<String, CommandArgument> commandArgs = new HashMap<>();
        if (args.length > 0) {
            commandArgs.put("currencyCode", new StringArg(args[0]));
        }

        Bukkit.getScheduler().runTaskAsynchronously(
            plugin, () -> {
                balanceCommand.execute(new PaperPlayer(player), commandArgs);
            }
        );

        return true;
    }
}
