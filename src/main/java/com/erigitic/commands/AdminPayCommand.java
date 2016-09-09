package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

/**
 * Created by Eric on 9/7/2015.
 */
public class AdminPayCommand implements CommandExecutor {
    private Logger logger;
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private Currency defaultCurrency;

    public AdminPayCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountManager = totalEconomy.getAccountManager();

        defaultCurrency = accountManager.getDefaultCurrency();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String strAmount = args.<String>getOne("amount").get();
        Player recipient = args.<Player>getOne("player").get();

        if (TotalEconomy.isNumeric(strAmount)) {
            if (!strAmount.contains("-")) {
                BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);
                TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();

                TransactionResult transactionResult = recipientAccount.deposit(accountManager.getDefaultCurrency(), amount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

                if (transactionResult.getResult() == ResultType.SUCCESS) {
                    src.sendMessage(Text.of(TextColors.GRAY, "You have sent ", TextColors.GOLD, defaultCurrency.format(amount),
                            TextColors.GRAY, " to ", TextColors.GOLD, recipient.getName()));

                    recipient.sendMessage(Text.of(TextColors.GRAY, "You have received ", TextColors.GOLD, defaultCurrency.format(amount),
                            TextColors.GRAY, " from ", TextColors.GOLD, src.getName(), "."));
                }
            } else {
                src.sendMessage(Text.of(TextColors.RED, "The amount must be positive."));
            }
        } else {
            src.sendMessage(Text.of(TextColors.RED, "The amount must only contain numbers and a single decimal point if needed."));
        }

        return CommandResult.success();
    }
}
