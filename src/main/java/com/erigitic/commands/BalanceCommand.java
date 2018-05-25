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
import com.erigitic.util.MessageManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class BalanceCommand implements CommandExecutor {
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private MessageManager messageManager;
    private Currency defaultCurrency;

    public BalanceCommand(TotalEconomy totalEconomy, AccountManager accountManager, MessageManager messageManager) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.messageManager = messageManager;

        defaultCurrency = totalEconomy.getDefaultCurrency();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player sender = (Player) src;
            TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(sender.getUniqueId()).get();
            Optional<String> optCurrencyName = args.getOne("currencyName");
            Map<String, String> messageValues = new HashMap<>();

            if (optCurrencyName.isPresent()) {
                Optional<Currency> optCurrency = totalEconomy.getTECurrencyRegistryModule().getById("totaleconomy:" + optCurrencyName.get().toLowerCase());

                if (optCurrency.isPresent()) {
                    TECurrency currency = (TECurrency) optCurrency.get();

                    messageValues.put("currency", currency.getName());
                    messageValues.put("amount", currency.format(playerAccount.getBalance(currency)).toPlain());

                    sender.sendMessage(messageManager.getMessage("command.balance.other", messageValues));
                } else {
                    throw new CommandException(Text.of(TextColors.RED, "[TE] The specified currency does not exist!"));
                }
            } else {
                messageValues.put("amount", defaultCurrency.format(playerAccount.getBalance(defaultCurrency)).toPlain());

                sender.sendMessage(messageManager.getMessage("command.balance.default", messageValues));
            }

            return CommandResult.success();
        } else {
            throw new CommandException(Text.of("[TE] This command can only be run by a player!"));
        }
    }
}
