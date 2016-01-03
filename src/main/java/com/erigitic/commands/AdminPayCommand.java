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
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

/**
 * Created by Erigitic on 9/7/2015.
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
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            Object playerArg = args.getOne("player").get();
            String strAmount = (String) args.getOne("amount").get();

            if (totalEconomy.isNumeric(strAmount)) {
                if (!strAmount.contains("-")) {
                    if (playerArg instanceof Player) {
                        Player recipient = (Player) playerArg;
                        BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);

                        TEAccount recipientAccount = (TEAccount) accountManager.getAccount(recipient.getUniqueId()).get();

                        TransactionResult transactionResult = recipientAccount.deposit(accountManager.getDefaultCurrency(), amount, Cause.of("Admin Pay"));

                        //TODO: Check for ResultType.FAILED?
                        if (transactionResult.getResult() == ResultType.SUCCESS) {
                            sender.sendMessage(Text.of(TextColors.GRAY, "You have sent ", TextColors.GOLD, defaultCurrency.getSymbol(),
                                    amount, TextColors.GRAY, " to ", TextColors.GOLD, recipient.getName()));

                            recipient.sendMessage(Text.of(TextColors.GRAY, "You have received ", TextColors.GOLD, defaultCurrency.getSymbol(),
                                    amount, TextColors.GRAY, " from ", TextColors.GOLD, sender.getName()));
                        }
                    }
                } else {
                    sender.sendMessage(Text.of(TextColors.RED, "The amount must be positive."));
                }
            } else {
                sender.sendMessage(Text.of(TextColors.RED, "The amount must only contain numbers and a single decimal point if needed."));
            }
        }

        return CommandResult.success();
    }
}
