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

package com.erigitic.config.account;

import com.erigitic.config.TECurrency;
import com.erigitic.config.TEEconomyTransactionEvent;
import com.erigitic.config.TETransactionResult;
import com.erigitic.config.TETransferResult;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.*;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.*;

/**
 * Account base class.
 * This class contains all storage independent methods and fields.
 *
 * This is extended by all storage dependent implementations of {@link UniqueAccount} filling in the missing methods.
 */
public abstract class TEAccountBase implements UniqueAccount {

    private static final BigDecimal ACCOUNT_MIN_VALUE = BigDecimal.ZERO;
    private static final BigDecimal ACCOUNT_MAX_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);

    protected TotalEconomy totalEconomy;
    protected UUID accountUUID;

    public TEAccountBase(TotalEconomy totalEconomy, UUID accountUUID) {
        this.totalEconomy = totalEconomy;
        this.accountUUID = accountUUID;
    }

    /**
     * Ensures the account itself has been persisted in the storage medium.
     */
    public abstract void create();

    public boolean checkExceedsLowerBound(BigDecimal value) {
        return ACCOUNT_MIN_VALUE.compareTo(value) < 0;
    }

    public boolean checkExceedsUpperBound(BigDecimal value) {
        return ACCOUNT_MAX_VALUE.compareTo(value) > 0;
    }

    /**
     * Add money to a balance.
     *
     * @param currency The balance to deposit money into
     * @param amount Amount to deposit
     * @param cause The cause of the transaction
     * @param contexts The contexts that the check occurred in
     * @return TransactionResult Result of the deposit
     */
    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal curBalance = getBalance(currency, contexts);
        BigDecimal newBalance = curBalance.add(amount);

        if (!checkExceedsUpperBound(newBalance)) {
            return setBalance(currency, newBalance, cause);
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_SPACE, TransactionTypes.DEPOSIT);
    }


    /**
     * Remove money from a balance.
     *
     * @param currency The balance to withdraw money from
     * @param amount Amount to withdraw
     * @param cause The cause of the transaction
     * @param contexts The contexts that the check occurred in
     * @return TransactionResult Result of the withdrawal
     */
    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal curBalance =  getBalance(currency, contexts);
        BigDecimal newBalance = curBalance.subtract(amount);

        if (!checkExceedsLowerBound(newBalance)) {
            return setBalance(currency, newBalance, cause);
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
    }

    /**
     * Transfer money between two TEAccount's.
     *
     * @param to Account to transfer money to
     * @param currency Type of currency to transfer
     * @param amount Amount to transfer
     * @param cause The cause of the transaction
     * @param contexts The contexts that the check occurred in
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
     * Get a player's balance for each currency type.
     *
     * @param contexts The contexts that the check occurred in
     * @return Map A map of the balances of each currency
     */
    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();

        for (Currency currency : totalEconomy.getCurrencies()) {
            balances.put(currency, getBalance(currency, contexts));
        }

        return balances;
    }

    /**
     * Resets all currency balances to their starting balances.
     *
     * @param cause The cause of the transaction
     * @param contexts The contexts that the check occurred in
     * @return Map Map of transaction results
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
     * Reset a currencies balance to its starting balance.
     *
     * @param currency The balance to reset
     * @param cause The cause of the transaction
     * @param contexts The contexts that the check occurred in
     * @return TransactionResult Result of the reset
     */
    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        if (currency instanceof TECurrency) {
            return setBalance(currency, ((TECurrency) currency).getStartingBalance(), cause);
        } else {
            throw new IllegalArgumentException("Cannot reset foreign currency! \"" + currency.getClass().getName() + "\"");
        }
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        if (currency instanceof TECurrency) {
            return ((TECurrency) currency).getStartingBalance();
        } else {
            throw new IllegalArgumentException("Cannot get default balance for foreign currency! \"" + currency.getClass().getName() + "\"");
        }
    }

    /**
     * Get the {@link UUID} of the account.
     *
     * @return UUID The UUID of the account
     */
    @Override
    public UUID getUniqueId() {
        return accountUUID;
    }

    /**
     * Get the account identifier.
     *
     * @return String The identifier
     */
    @Override
    public String getIdentifier() {
        return getUniqueId().toString();
    }


    /**
     * @return Currently an empty context.
     */
    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<>();
    }

    /**
     * Whether or not this is a virtual (company or similar) account or belongs to a player.
     */
    public boolean isVirtual() {
        return totalEconomy.getUserStorageService().get(accountUUID).isPresent();
    }

    /**
     * Gets the display name associated with the account.
     *
     * @return Text The display name
     */
    @Override
    public Text getDisplayName() {
        Optional<User> optUser = totalEconomy.getUserStorageService().get(accountUUID);
        return Text.of(optUser.map(User::getName).orElse("<ACCOUNT_NAME>"));
    }
}
