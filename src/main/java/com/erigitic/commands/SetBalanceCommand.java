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
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

/**
 * Created by Eric on 8/7/2015.
 */
public class SetBalanceCommand implements CommandExecutor {
    private AccountManager accountManager;

    public SetBalanceCommand(TotalEconomy totalEconomy) {
        accountManager = totalEconomy.getAccountManager();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player recipient = args.<Player>getOne("player").get();
        BigDecimal amount = new BigDecimal(args.<String>getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);
        Currency defaultCurrency = accountManager.getDefaultCurrency();

        TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();

        recipientAccount.setBalance(accountManager.getDefaultCurrency(), amount, Cause.of(NamedCause.of("TotalEconomy", this)));

        src.sendMessage(Text.of(TextColors.GRAY, "You set ", recipient.getName(), "\'s balance to ", TextColors.GOLD, defaultCurrency.format(amount)));

        return CommandResult.success();
    }
}
