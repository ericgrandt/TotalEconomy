package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.jobs.TEJobs;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 8/7/2015.
 */
public class SetBalanceCommand implements CommandExecutor {
    private AccountManager accountManager;

    public SetBalanceCommand(TotalEconomy totalEconomy) {
        accountManager = totalEconomy.getAccountManager();
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player sender = ((Player) src).getPlayer().get();
        Player recipient = (Player) args.getOne("player").get();
        BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);

        accountManager.setBalance(recipient.getUniqueId(), amount);

        sender.sendMessage(Texts.of(TextColors.GRAY, "You set ", recipient.getName(), "\'s balance to ", TextColors.GOLD, amount));

        return CommandResult.success();
    }
}
