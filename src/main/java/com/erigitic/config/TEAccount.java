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
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.*;

public class TEAccount implements UniqueAccount {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private UUID uuid;
    private Logger logger;

    private ConfigurationNode accountConfig;

    public TEAccount(TotalEconomy totalEconomy, AccountManager accountManager, UUID uuid) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.uuid = uuid;

        accountConfig = accountManager.getAccountConfig();
    }

    @Override
    public Text getDisplayName() {
        if (totalEconomy.getUserStorageService().get(uuid).isPresent())
            return Text.of(totalEconomy.getUserStorageService().get(uuid).get().getName());

        return Text.of("PLAYER NAME");
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return totalEconomy.getStartingBalance();
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        if (accountConfig.getNode(uuid.toString(), currencyName + "-balance").getValue() != null) {
            return true;
        }

        return false;
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal balance = new BigDecimal(accountConfig.getNode(uuid.toString(), currencyName + "-balance").getString());

            return balance;
        }

        return BigDecimal.ZERO;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        return new HashMap<Currency, BigDecimal>();
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;

        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();

            accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(amount.setScale(2, BigDecimal.ROUND_DOWN));
            accountManager.saveAccountConfig();

            transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
            totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

            return transactionResult;
        }

        transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult = new TETransactionResult(this, accountManager.getDefaultCurrency(), BigDecimal.ZERO, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        //TODO: Do something different here?
        Map result = new HashMap<>();
        result.put(accountManager.getDefaultCurrency(), transactionResult);

        return result;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return setBalance(currency, BigDecimal.ZERO, cause);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;

        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal curBalance = getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.add(amount);
            // Is the new balance higher ?
            boolean deposit = newBalance.compareTo(curBalance) == 1;

            boolean denied = false;
            // Deny transaction if maxMoneyCap has been triggered
            if (totalEconomy.getMaxMoneyCap().isPresent()) {
                if (newBalance.compareTo(totalEconomy.getMaxMoneyCap().get()) == 1) {
                    denied = true;
                }
            }
            // Deny transaction if minMoneyCap has been triggered -> Skip if already denied
            if (!denied && totalEconomy.getMaxMoneyCap().isPresent()) {
                if (totalEconomy.getMinMoneyCap().get().compareTo(newBalance) == 1) {
                    denied = true;
                }
            }

            if (!denied) {
                //TODO: May want to abstract this into a class (TEAccountManager?) (suggestion)
                accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN));
                accountManager.saveAccountConfig();

                transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS,
                        deposit ? TransactionTypes.DEPOSIT : TransactionTypes.WITHDRAW);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));
            } else {
                transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_SPACE, deposit ? TransactionTypes.DEPOSIT : TransactionTypes.WITHDRAW);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));
            }
            return transactionResult;
        }


        transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));
        return transactionResult;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;

        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal curBalance =  getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.subtract(amount);

            boolean denied = false;
            // Deny transaction if maxMoneyCap has been triggered
            if (totalEconomy.getMaxMoneyCap().isPresent()) {
                if (newBalance.compareTo(totalEconomy.getMaxMoneyCap().get()) == 1) {
                    denied = true;
                }
            }
            // Deny transaction if minMoneyCap has been triggered -> Skip if already denied
            if (!denied && totalEconomy.getMaxMoneyCap().isPresent()) {
                if (totalEconomy.getMinMoneyCap().get().compareTo(newBalance) == 1) {
                    denied = true;
                }
            }
            if (denied) {
                transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_SPACE, TransactionTypes.WITHDRAW);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));
                return transactionResult;
            }

            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN));
                accountManager.saveAccountConfig();

                transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

                return transactionResult;
            } else {
                transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.DEPOSIT);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

                return transactionResult;
            }
        }

        transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransferResult transferResult;

        if (hasBalance(currency, contexts)) {
            BigDecimal curBalance = getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.subtract(amount);

            TransactionResult fromRes = withdraw(currency, amount, cause, contexts);
            if (fromRes.getResult() != ResultType.SUCCESS) {
                transferResult = new TETransferResult(this, to, currency, amount, contexts, fromRes.getResult(), fromRes.getType());
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));
                return transferResult;
            }
            TransactionResult toRes = to.deposit(currency, amount, cause, contexts);
            if (toRes.getResult() != ResultType.SUCCESS) {
                //Repay sender if failed
                deposit(currency, amount, cause, contexts);
                transferResult = new TETransferResult(this, to, currency, amount, contexts, toRes.getResult(), toRes.getType());
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));
                return transferResult;
            }
            transferResult = new TETransferResult(fromRes, toRes);
            totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));
            return transferResult;
        }

        transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));
        return transferResult;
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<Context>();
    }
}
