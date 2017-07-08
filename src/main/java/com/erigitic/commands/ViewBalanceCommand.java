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
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class ViewBalanceCommand implements CommandExecutor {
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private Currency defaultCurrency;

    public ViewBalanceCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        accountManager = totalEconomy.getAccountManager();

        defaultCurrency = totalEconomy.getDefaultCurrency();
    }

    @Override
    public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
        User recipient = args.<User>getOne("player").get();
        Optional<String> optCurrencyName = args.getOne("currencyName");
        TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();
        BigDecimal balance;
        Text balanceText;
        TransactionResult transactionResult;

        if (optCurrencyName.isPresent()) {
            Optional<Currency> optCurrency = totalEconomy.getTECurrencyRegistryModule().getById("totaleconomy:" + optCurrencyName.get().toLowerCase());

            if (optCurrency.isPresent()) {
                balance = recipientAccount.getBalance(optCurrency.get());

                balanceText = Text.of(TextColors.GRAY, recipient.getName(), "'s ", optCurrency.get().getDisplayName(), " Balance: ", TextColors.GOLD, optCurrency.get().format(balance));
                transactionResult = recipientAccount.setBalance(optCurrency.get(), balance, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
            } else {
                throw new CommandException(Text.of(TextColors.RED, "[TE] The specified currency does not exist!"));
            }
        } else {
            balance = recipientAccount.getBalance(defaultCurrency);

            balanceText = Text.of(TextColors.GRAY, recipient.getName(), "'s ", defaultCurrency.getDisplayName(), " Balance: ", TextColors.GOLD, defaultCurrency.format(balance));
            transactionResult = recipientAccount.setBalance(defaultCurrency, balance, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
        }

        if (transactionResult.getResult() == ResultType.SUCCESS) {
            sender.sendMessage(Text.of(balanceText));

            return CommandResult.success();
        } else {
            throw new CommandException(Text.of("[TE] An error occurred setting a player's balance!"));
        }
    }
}
