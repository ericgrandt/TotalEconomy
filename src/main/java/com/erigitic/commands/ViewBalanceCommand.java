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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

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

            if (playerArg instanceof User) {
                User recipient = (User) playerArg;

                TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();

                Text symbol = accountManager.getDefaultCurrency().getSymbol();
                BigDecimal balance = recipientAccount.getBalance(accountManager.getDefaultCurrency());
                Text formattedBal = accountManager.getDefaultCurrency().format(balance);

                sender.sendMessage(Text.of(TextColors.GRAY, recipient.getName(), "'s Balance: ", TextColors.GOLD, symbol, formattedBal));
            }
        }

        return CommandResult.success();
    }
}
