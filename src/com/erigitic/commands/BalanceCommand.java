package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

/**
 * Created by Erigitic on 5/4/2015.
 */
public class BalanceCommand implements CommandExecutor {
    private Logger logger;
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    public BalanceCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountManager = totalEconomy.getAccountManager();
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player sender = ((Player) src).getPlayer().get();

        sender.sendMessage(Texts.of(TextColors.GRAY, "Balance: ", TextColors.GOLD, totalEconomy.getCurrencySymbol(), accountManager.getBalance(sender)));

        return CommandResult.success();
    }
}
