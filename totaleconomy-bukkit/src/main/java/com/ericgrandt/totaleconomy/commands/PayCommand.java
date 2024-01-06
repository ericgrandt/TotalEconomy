package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.models.TransferResult;
import com.ericgrandt.totaleconomy.models.TransferResult.ResultType;
import com.ericgrandt.totaleconomy.services.BalanceService;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {
    private final Logger logger;
    private final BukkitWrapper bukkitWrapper;
    private final EconomyImpl economy;
    private final BalanceService balanceService;

    public PayCommand(Logger logger, BukkitWrapper bukkitWrapper, EconomyImpl economy, BalanceService balanceService) {
        this.logger = logger;
        this.bukkitWrapper = bukkitWrapper;
        this.economy = economy;
        this.balanceService = balanceService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this command");
            return false;
        }

        if (args.length != 2) {
            return false;
        }

        Player targetPlayer = bukkitWrapper.getPlayerExact(args[0]);
        CompletableFuture.runAsync(() -> onCommandHandler(player, targetPlayer, args[1]));

        return true;
    }

    public void onCommandHandler(Player player, Player targetPlayer, String amountArg) {
        if (targetPlayer == null) {
            player.sendMessage("Invalid player specified");
            return;
        }
        if (!isValidDouble(amountArg)) {
            player.sendMessage("Invalid amount specified");
            return;
        }

        UUID playerUUID = player.getUniqueId();
        UUID targetUUID = targetPlayer.getUniqueId();
        if (playerUUID == targetUUID) {
            player.sendMessage("You cannot pay yourself");
            return;
        }

        double amount = scaleAmountToNumFractionDigits(Double.parseDouble(amountArg));
        try {
            TransferResult transferResult = balanceService.transfer(playerUUID, targetUUID, amount);
            if (transferResult.resultType() == ResultType.FAILURE) {
                player.sendMessage(transferResult.message());
                return;
            }
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                "An exception occurred during the handling of the pay command.",
                e
            );
            player.sendMessage("Error executing command. Contact an administrator.");
            return;
        }

        String formattedAmount = economy.format(amount);
        player.sendMessage(String.format("You sent %s to %s", formattedAmount, targetPlayer.getName()));
        targetPlayer.sendMessage(String.format("You received %s from %s", formattedAmount, player.getName()));
    }

    private boolean isValidDouble(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double scaleAmountToNumFractionDigits(double amount) {
        CurrencyDto defaultCurrency = economy.getDefaultCurrency();
        return BigDecimal.valueOf(amount).setScale(
            defaultCurrency.numFractionDigits(),
            RoundingMode.DOWN
        ).doubleValue();
    }
}
