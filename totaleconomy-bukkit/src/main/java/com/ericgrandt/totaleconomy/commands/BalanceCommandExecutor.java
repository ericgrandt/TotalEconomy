package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.BalanceCommand;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.commonimpl.BukkitPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommandExecutor implements CommandExecutor {
    private final CommonEconomy economy;
    private final CurrencyDto currency;

    // TODO: Take in BalanceCommand instead
    public BalanceCommandExecutor(final CommonEconomy economy, final CurrencyDto currency) {
        this.economy = economy;
        this.currency = currency;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        return new BalanceCommand(economy, currency).execute(new BukkitPlayer(player), null);
    }
}
