package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.BalanceCommand;
import com.ericgrandt.totaleconomy.commonimpl.BukkitPlayer;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommandExecutor implements CommandExecutor {
    private final EconomyImpl economy;

    public BalanceCommandExecutor(final EconomyImpl economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        return new BalanceCommand(economy).execute(new BukkitPlayer(player), null);
    }
}
