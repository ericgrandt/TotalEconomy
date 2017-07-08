/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.commands;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.config.TECurrency;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayCommand implements CommandExecutor {
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private Currency defaultCurrency;

    public PayCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();

        defaultCurrency = totalEconomy.getDefaultCurrency();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String amountStr = (String) args.getOne("amount").get();
        Player recipient = (Player) args.getOne("player").get();
        Optional<String> optCurrencyName = args.getOne("currencyName");

        if (src instanceof Player) {
            Player sender = (Player) src;

            if (sender.getUniqueId().equals(recipient.getUniqueId())) {
                throw new CommandException(Text.of("[TE] You cannot pay yourself!"));
            }

            Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
            Matcher m = amountPattern.matcher(amountStr);

            if (m.matches()) {
                BigDecimal amount = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_DOWN);
                Text amountText;
                TransferResult transferResult;

                TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(sender.getUniqueId()).get();
                TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();

                if (optCurrencyName.isPresent()) {
                    Optional<Currency> optCurrency = totalEconomy.getTECurrencyRegistryModule().getById("totaleconomy:" + optCurrencyName.get().toLowerCase());

                    if (optCurrency.isPresent()) {
                        TECurrency teCurrency = (TECurrency) optCurrency.get();

                        if (teCurrency.isTransferable()) {
                            amountText = Text.of(optCurrency.get().format(amount));
                            transferResult = playerAccount.transfer(recipientAccount, optCurrency.get(), amount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                        } else {
                            throw new CommandException(Text.of("[TE] ", teCurrency.getPluralDisplayName(), " can't be transferred!"));
                        }
                    } else {
                        throw new CommandException(Text.of("[TE] The specified currency does not exist!"));
                    }
                } else {
                    amountText = Text.of(defaultCurrency.format(amount));
                    transferResult = playerAccount.transfer(recipientAccount, totalEconomy.getDefaultCurrency(), amount, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
                }

                if (transferResult.getResult() == ResultType.SUCCESS) {
                    sender.sendMessage(Text.of(TextColors.GRAY, "You have sent ", TextColors.GOLD, amountText,
                            TextColors.GRAY, " to ", TextColors.GOLD, recipient.getName(), TextColors.GRAY, "."));

                    recipient.sendMessage(Text.of(TextColors.GRAY, "You have received ", TextColors.GOLD, amountText,
                            TextColors.GRAY, " from ", TextColors.GOLD, sender.getName(), TextColors.GRAY, "."));

                    return CommandResult.success();
                } else if (transferResult.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                    throw new CommandException(Text.of("[TE] Insufficient funds!"));
                } else {
                    throw new CommandException(Text.of("[TE] An error occurred while paying another player!"));
                }
            } else {
                throw new CommandException(Text.of("[TE] Invalid amount! Must be a positive number!"));
            }
        } else {
            throw new CommandException(Text.of("[TE] This command can only be run by a player!"));
        }
    }
}
