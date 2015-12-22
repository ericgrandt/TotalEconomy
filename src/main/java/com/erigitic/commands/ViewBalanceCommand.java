package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Erigitic on 12/22/2015.
 */
public class ViewBalanceCommand implements CommandExecutor {
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    public ViewBalanceCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            Object playerArg = args.getOne("player").get();

            if (playerArg instanceof Player) {
                Player recipient = (Player) playerArg;
                sender.sendMessage(Texts.of(TextColors.GRAY, recipient.getName(), "'s Balance: ", TextColors.GOLD, totalEconomy.getCurrencySymbol(), accountManager.getBalance(recipient.getUniqueId())));
            }
        }

        return CommandResult.success();
    }
}
