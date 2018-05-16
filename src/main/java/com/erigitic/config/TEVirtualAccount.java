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

package com.erigitic.config;

import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.SqlManager;
import com.erigitic.sql.SqlQuery;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

public class TEVirtualAccount implements VirtualAccount {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private String identifier;
    private SqlManager sqlManager;

    private ConfigurationNode accountConfig;

    private boolean databaseActive;

    public TEVirtualAccount(TotalEconomy totalEconomy, AccountManager accountManager, String identifier) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.identifier = identifier;

        accountConfig = accountManager.getAccountConfig();
        databaseActive = totalEconomy.isDatabaseEnabled();

        if (databaseActive) {
            sqlManager = totalEconomy.getSqlManager();
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.of(identifier);
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return ((TECurrency) currency).getStartingBalance();
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        if (databaseActive) {
            SqlQuery sqlQuery = SqlQuery.builder(sqlManager.dataSource)
                    .select(currencyName + "_balance")
                    .from("virtual_accounts")
                    .where("uid")
                    .equals(identifier)
                    .build();

            return sqlQuery.recordExists();
        } else {
            return accountConfig.getNode(identifier, currencyName + "-balance").getValue() != null;
        }
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();

            if (databaseActive) {
                SqlQuery sqlQuery = SqlQuery.builder(sqlManager.dataSource)
                        .select(currencyName + "_balance")
                        .from("virtual_accounts")
                        .where("uid")
                        .equals(identifier)
                        .build();

                return sqlQuery.getBigDecimal(BigDecimal.ZERO);
            } else {
                BigDecimal balance = new BigDecimal(accountConfig.getNode(identifier, currencyName + "-balance").getString());

                return balance;
            }
        }

        return BigDecimal.ZERO;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        return new HashMap<>();
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        if (hasBalance(currency, contexts)) {
            BigDecimal delta = amount.subtract(getBalance(currency));
            TransactionType transactionType = delta.compareTo(BigDecimal.ZERO) >= 0 ? TransactionTypes.DEPOSIT : TransactionTypes.WITHDRAW;

            if (databaseActive) {
                SqlQuery sqlQuery = SqlQuery.builder(sqlManager.dataSource)
                        .update("virtual_accounts")
                        .set(currencyName + "_balance")
                        .equals(amount.setScale(2, BigDecimal.ROUND_DOWN).toPlainString())
                        .where("uid")
                        .equals(identifier)
                        .build();

                if (sqlQuery.getRowsAffected() > 0) {
                    transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.SUCCESS, transactionType);
                } else {
                    transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.FAILED, transactionType);
                }
            } else {
                accountConfig.getNode(identifier, currencyName + "-balance").setValue(amount.setScale(2, BigDecimal.ROUND_DOWN));
                accountManager.requestConfigurationSave();

                transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.SUCCESS, transactionType);
            }
        } else {
            transactionResult = new TETransactionResult(this, currency, BigDecimal.ZERO, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        }

        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult = new TETransactionResult(this, totalEconomy.getDefaultCurrency(), BigDecimal.ZERO, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        Map result = new HashMap<>();
        result.put(totalEconomy.getDefaultCurrency(), transactionResult);

        return result;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return setBalance(currency, BigDecimal.ZERO, cause);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal curBalance = getBalance(currency, contexts);
        BigDecimal newBalance = curBalance.add(amount);

        return setBalance(currency, newBalance, cause);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal curBalance =  getBalance(currency, contexts);
        BigDecimal newBalance = curBalance.subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            return setBalance(currency, newBalance, cause);
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransferResult transferResult;

        if (hasBalance(currency, contexts)) {
            BigDecimal curBalance = getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.subtract(amount);

            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                withdraw(currency, amount, cause, contexts);

                if (to.hasBalance(currency)) {
                    to.deposit(currency, amount, cause, contexts);

                    transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.TRANSFER);
                    totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

                    return transferResult;
                } else {
                    transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
                    totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

                    return transferResult;
                }
            } else {
                transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.TRANSFER);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

                return transferResult;
            }
        }

        transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

        return transferResult;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<>();
    }
}
