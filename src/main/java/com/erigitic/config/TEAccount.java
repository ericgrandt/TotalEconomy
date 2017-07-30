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
import com.erigitic.sql.SQLHandler;
import com.erigitic.sql.SQLQuery;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.*;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.*;

public class TEAccount implements UniqueAccount {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private UUID uuid;
    private SQLHandler sqlHandler;

    private ConfigurationNode accountConfig;

    private boolean databaseActive;

    /**
     * Constructor for the TEAccount class. Manages a unique account, identified by a {@link UUID}, that contains balances for each {@link Currency}.
     *
     * @param totalEconomy Main plugin class
     * @param accountManager {@link AccountManager} object
     * @param uuid The UUID of the account
     */
    public TEAccount(TotalEconomy totalEconomy, AccountManager accountManager, UUID uuid) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.uuid = uuid;

        accountConfig = accountManager.getAccountConfig();
        databaseActive = totalEconomy.isDatabaseActive();

        if (databaseActive)
            sqlHandler = totalEconomy.getSqlHandler();
    }

    /**
     * Gets the display name associated with the account
     *
     * @return Text The display name
     */
    @Override
    public Text getDisplayName() {
        if (totalEconomy.getUserStorageService().get(uuid).isPresent())
            return Text.of(totalEconomy.getUserStorageService().get(uuid).get().getName());

        return Text.of("PLAYER NAME");
    }

    /**
     * Gets the default balance
     *
     * @param currency Currency to get the default balance for
     * @return BigDecimal Default balance
     */
    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return ((TECurrency) currency).getStartingBalance();
    }

    /**
     * Determines if a balance exists for a {@link Currency}
     *
     * @param currency Currency type to be checked for
     * @param contexts
     * @return boolean If a balance exists for the specified currency
     */
    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        if (databaseActive) {
            SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                    .select(currencyName + "_balance")
                    .from("accounts")
                    .where("uid")
                    .equals(uuid.toString())
                    .build();

            return sqlQuery.recordExists();
        } else {
            return accountConfig.getNode(uuid.toString(), currencyName + "-balance").getValue() != null;
        }
    }

    /**
     * Gets the balance of a {@link Currency}
     *
     * @param currency The currency to get the balance of
     * @param contexts
     * @return BigDecimal The balance
     */
    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();

            if (databaseActive) {
                SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                        .select(currencyName + "_balance")
                        .from("accounts")
                        .where("uid")
                        .equals(uuid.toString())
                        .build();

                return sqlQuery.getBigDecimal(BigDecimal.ZERO);
            } else {
                BigDecimal balance = new BigDecimal(accountConfig.getNode(uuid.toString(), currencyName + "-balance").getString());

                return balance;
            }
        }

        return BigDecimal.ZERO;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        return new HashMap<>();
    }

    /**
     * Sets the balance of a {@link Currency}
     *
     * @param currency Currency to set the balance of
     * @param amount Amount to set the balance to
     * @param cause
     * @param contexts
     * @return
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        // If the amount is greater then the money cap, set the amount to the money cap
        amount = amount.min(totalEconomy.getMoneyCap());

        if (hasBalance(currency, contexts)) {
            BigDecimal delta = amount.subtract(getBalance(currency));
            TransactionType transactionType;

            if (delta.compareTo(BigDecimal.ZERO) >= 0) {
                transactionType = TransactionTypes.DEPOSIT;
            } else {
                transactionType = TransactionTypes.WITHDRAW;
            }

            if (databaseActive) {
                SQLQuery sqlQuery = SQLQuery.builder(sqlHandler.dataSource)
                        .update("accounts")
                        .set(currencyName + "_balance")
                        .equals(amount.setScale(2, BigDecimal.ROUND_DOWN).toPlainString())
                        .where("uid")
                        .equals(uuid.toString())
                        .build();

                if (sqlQuery.getRowsAffected() > 0) {
                    transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.SUCCESS, transactionType);
                } else {
                    transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.FAILED, transactionType);
                }
            } else {
                accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(amount.setScale(2, BigDecimal.ROUND_DOWN));
                accountManager.saveAccountConfig(false);

                transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.SUCCESS, transactionType);
            }
        } else {
            transactionResult = new TETransactionResult(this, currency, BigDecimal.ZERO, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        }

        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    /**
     * Resets all currency balances to their starting balances
     *
     * @param cause
     * @param contexts
     * @return Map<Currency, TransactionResult> Map of transaction results
     */
    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        Map<Currency, TransactionResult> result = new HashMap<>();

        for (Currency currency : totalEconomy.getCurrencies()) {
            result.put(currency, resetBalance(currency, cause, contexts));
        }

        return result;
    }

    /**
     * Reset a currencies balance to its starting balance
     *
     * @param currency The balance to reset
     * @param cause
     * @param contexts
     * @return TransactionResult Result of the reset
     */
    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return setBalance(currency, ((TECurrency) currency).getStartingBalance(), cause);
    }

    /**
     * Add money to a balance
     *
     * @param currency The balance to deposit money into
     * @param amount Amount to deposit
     * @param cause
     * @param contexts
     * @return TransactionResult Result of the deposit
     */
    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal curBalance = getBalance(currency, contexts);
        BigDecimal newBalance = curBalance.add(amount);

        return setBalance(currency, newBalance, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
    }

    /**
     * Remove money from a balance
     *
     * @param currency The balance to withdraw money from
     * @param amount Amount to withdraw
     * @param cause
     * @param contexts
     * @return TransactionResult Result of the withdrawal
     */
    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal curBalance =  getBalance(currency, contexts);
        BigDecimal newBalance = curBalance.subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            return setBalance(currency, newBalance, Cause.of(NamedCause.of("TotalEconomy", totalEconomy.getPluginContainer())));
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
    }

    /**
     * Transfer money between two TEAccount's
     *
     * @param to Account to transfer money to
     * @param currency Type of currency to transfer
     * @param amount Amount to transfer
     * @param cause
     * @param contexts
     * @return TransactionResult Result of the reset
     */
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

    /**
     * Get the account identifier
     *
     * @return String The identifier
     */
    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    /**
     * Get the {@link UUID} of the account
     *
     * @return UUID The UUID of the account
     */
    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<>();
    }
}
