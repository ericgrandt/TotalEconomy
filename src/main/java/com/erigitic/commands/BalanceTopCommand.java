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
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.*;

public class BalanceTopCommand implements CommandExecutor {
    private TotalEconomy totalEconomy;
    private AccountManager accountManager;

    private PaginationService paginationService = Sponge.getServiceManager().provideUnchecked(PaginationService.class);
    private PaginationList.Builder builder = paginationService.builder();

    public BalanceTopCommand(TotalEconomy totalEconomy, AccountManager accountManager) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ConfigurationNode accountNode = accountManager.getAccountConfig();
        List<Text> accountBalances = new ArrayList<>();
        Map<String, BigDecimal> accountBalancesMap = new HashMap<>();
        Currency defaultCurrency = totalEconomy.getDefaultCurrency();

        accountNode.getChildrenMap().keySet().forEach(accountUUID -> {
            UUID uuid;

            // Check if the account is virtual or not. If virtual, skip the rest of the execution and move on to next account.
            try {
                uuid = UUID.fromString(accountUUID.toString());
            } catch (IllegalArgumentException e) {
                return;
            }

            TEAccount playerAccount = (TEAccount) accountManager.getOrCreateAccount(uuid).get();
            Text playerName = playerAccount.getDisplayName();

            accountBalancesMap.put(playerName.toPlain(), playerAccount.getBalance(defaultCurrency));
        });

        accountBalancesMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> accountBalances.add(Text.of(TextColors.GRAY, entry.getKey(), ": ", TextColors.GOLD, defaultCurrency.format(entry.getValue()).toPlain())));

        builder.title(Text.of(TextColors.GOLD, "Top 10 Balances"))
                .contents(accountBalances)
                .sendTo(src);

        return CommandResult.success();
    }
}
