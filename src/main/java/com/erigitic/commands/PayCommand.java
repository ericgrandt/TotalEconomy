package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
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
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

/**
 * Created by Eric on 5/3/2015.
 */
public class PayCommand implements CommandExecutor {
    private Logger logger;
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private Currency defaultCurrency;

    public PayCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        accountManager = totalEconomy.getAccountManager();

        defaultCurrency = accountManager.getDefaultCurrency();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        //TODO: This makes my eyes hurt. Come back later to possibly make it look pretty.
        if (src instanceof Player) {
            Player sender = ((Player) src).getPlayer().get();
            Object playerArg = args.getOne("player").get();
            String strAmount = (String) args.getOne("amount").get();

            if (totalEconomy.isNumeric(strAmount)) {
                // Check for a negative number
                if (!strAmount.contains("-")) {
                    if (playerArg instanceof Player) {
                        Player recipient = (Player) playerArg;
                        BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);

                        TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(sender.getUniqueId()).get();
                        TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();

                        TransferResult transferResult = playerAccount.transfer(recipientAccount, accountManager.getDefaultCurrency(), amount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

                        if (transferResult.getResult() == ResultType.SUCCESS) {
                            sender.sendMessage(Text.of(TextColors.GRAY, "You have sent ", TextColors.GOLD, defaultCurrency.format(amount),
                                    TextColors.GRAY, " to ", TextColors.GOLD, recipient.getName(), "."));

                            recipient.sendMessage(Text.of(TextColors.GRAY, "You have received ", TextColors.GOLD, defaultCurrency.format(amount),
                                    TextColors.GRAY, " from ", TextColors.GOLD, sender.getName(), "."));
                        } else if (transferResult.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                            sender.sendMessage(Text.of(TextColors.RED, "Insufficient funds."));
                        }
                    }
                } else {
                    sender.sendMessage(Text.of(TextColors.RED, "The amount must be positive."));
                }
            } else {
                sender.sendMessage(Text.of(TextColors.RED, "The amount must only contain numbers and a single decimal point if needed."));
            }
        } else if (src instanceof ConsoleSource || src instanceof CommandBlockSource) {
            Object playerArg = args.getOne("player").get();
            String strAmount = (String) args.getOne("amount").get();

            if (playerArg instanceof Player) {
                Player recipient = (Player) playerArg;
                BigDecimal amount = new BigDecimal((String) args.getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);
                Text amountText = Text.of(defaultCurrency.format(amount).toPlain().replace("-", ""));

                TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();

                TransactionResult transactionResult = recipientAccount.deposit(accountManager.getDefaultCurrency(), amount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));

                if (transactionResult.getResult() == ResultType.SUCCESS) {
                    if (!strAmount.contains("-")) {
                        recipient.sendMessage(Text.of(TextColors.GRAY, "You have received ", TextColors.GOLD, amountText,
                                TextColors.GRAY, " from ", TextColors.GOLD, "SERVER."));
                    } else {
                        recipient.sendMessage(Text.of(TextColors.GOLD, amountText, TextColors.GRAY, " has been removed from your account by the ",
                                TextColors.GOLD, "SERVER."));
                    }
                }
            }
        }

        return CommandResult.success();
    }
}
