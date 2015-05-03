package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 5/3/2015.
 */
public class PayCommand implements CommandExecutor {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    public PayCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        accountManager.removeFromBalance(((Player) src).getPlayer().get(), new BigDecimal("1.01"));

        totalEconomy.getLogger().info("" + accountManager.getBalance(((Player) src).getPlayer().get()));

        return CommandResult.success();
    }
}
