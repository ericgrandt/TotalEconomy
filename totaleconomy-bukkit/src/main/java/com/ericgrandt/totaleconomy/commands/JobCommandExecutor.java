package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.JobCommand;
import com.ericgrandt.totaleconomy.commonimpl.BukkitPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JobCommandExecutor implements CommandExecutor {
    private final JobCommand jobCommand;

    public JobCommandExecutor(final JobCommand jobCommand) {
        this.jobCommand = jobCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        return jobCommand.execute(new BukkitPlayer(player), null);
    }
}
