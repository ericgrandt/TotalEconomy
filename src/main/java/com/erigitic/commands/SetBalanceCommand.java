package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
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
        //Declare response for context
        Text response = Text.of("TE: Report an issue on GitHub if you get to see this!");

        Player recipient = (Player) args.getOne("player").get();
        //Wish this would've worked, but it doesn't...
        if (recipient.isOnline()) {
            //Player online -> Change balance
            BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);
            Text symbol = accountManager.getDefaultCurrency().getSymbol();

            TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();

            recipientAccount.setBalance(accountManager.getDefaultCurrency(), amount, Cause.of(NamedCause.of("TotalEconomy", this)));

            //Format/Set reply:
            response = Text.of(TextColors.GRAY, "You set ", recipient.getName(), "\'s balance to ", TextColors.GOLD, symbol, amount);
        } else {
            //TODO:Implement a way of accessing accounts of offline players
            //Player NOT online: Show an error
            response = Text.of(TextColors.RED, "Player ", args.getOne("player"), " not found!");
        }

        //Fixes: #57
        if (src instanceof Player) {
            //Sent from player -> reply to player
            ((Player) src).getPlayer().get().sendMessage(response);
        } else if (src instanceof ConsoleSource) {
            //Sent from console -> reply to console
            ((ConsoleSource) src).sendMessage(response);
        } else if (src instanceof CommandBlockSource) {
            //Sent from commandBlock -> reply to commandBlock
            ((CommandBlockSource) src).sendMessage(response);
        }

        return CommandResult.success();
    }
}
