package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 8/7/2015.
 */
public class SetBalanceCommand implements CommandExecutor {
    private AccountManager accountManager;

    public SetBalanceCommand(TotalEconomy totalEconomy) {
        accountManager = totalEconomy.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player sender = ((Player) src).getPlayer().get();
        Player recipient = (Player) args.getOne("player").get();
        BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);
        Text symbol = accountManager.getDefaultCurrency().getSymbol();

        TEAccount recipientAccount = (TEAccount) accountManager.getAccount(recipient.getUniqueId()).get();

        recipientAccount.setBalance(accountManager.getDefaultCurrency(), amount, Cause.of("Set Balance"));

        sender.sendMessage(Text.of(TextColors.GRAY, "You set ", recipient.getName(), "\'s balance to ", TextColors.GOLD, symbol, amount));

        return CommandResult.success();
    }
}
