package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.command.CommonParameter;
import com.ericgrandt.totaleconomy.common.command.PayCommand;
import com.ericgrandt.totaleconomy.commonimpl.BukkitPlayer;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import java.math.BigDecimal;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommandExecutor implements CommandExecutor {
    private final PayCommand payCommand;
    private final BukkitWrapper bukkitWrapper;

    public PayCommandExecutor(
        final PayCommand payCommand,
        final BukkitWrapper bukkitWrapper
    ) {
        this.payCommand = payCommand;
        this.bukkitWrapper = bukkitWrapper;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        Player toPlayer = bukkitWrapper.getPlayerExact(args[0]);
        String amountArg = args[1];
        if (!isValidDouble(amountArg)) {
            player.sendMessage("Invalid amount specified");
            return false;
        }

        Map<String, CommonParameter<?>> argsMap = Map.of(
            "toPlayer", new CommonParameter<>(new BukkitPlayer(toPlayer)),
            "amount", new CommonParameter<>(BigDecimal.valueOf(Double.parseDouble(amountArg)))
        );

        return payCommand.execute(new BukkitPlayer(player), argsMap);
    }

    private boolean isValidDouble(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
