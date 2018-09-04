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

import com.erigitic.config.AccountManager;
import com.erigitic.config.TETransactionResult;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class TEConfigAccount extends TEAccountBase {

    private static final String CONF_BALANCES_SUBKEY = "balances";

    private AccountManager accountManager;

    public TEConfigAccount(TotalEconomy totalEconomy, UUID accountUUID) {
        super(totalEconomy, accountUUID);
        this.accountManager = totalEconomy.getAccountManager();
    }

    @Override
    public void create() {
        // TODO: Implement #create on ConfigAccount.
    }

    /**
     * @see org.spongepowered.api.service.economy.account.UniqueAccount#hasBalance(Currency)
     */
    @Override
    public boolean hasBalance(Currency currency) {
        return super.hasBalance(currency);
    }

    /**
     * Whether or not the account currently has set a value for the given balance.
     */
    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return accountManager.getAccountConfig().getNode(accountUUID.toString(), currency.getId()).isVirtual();
    }

    /**
     * @see org.spongepowered.api.service.economy.account.UniqueAccount#getBalance(Currency)
     */
    @Override
    public BigDecimal getBalance(Currency currency) {
        return super.getBalance(currency);
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        Object value = accountManager.getAccountConfig()
                .getNode(accountUUID.toString(), CONF_BALANCES_SUBKEY, currency.getId())
                .getValue(() -> null);
        return value instanceof Double ?
                BigDecimal.valueOf(((Double) value))
                : getDefaultBalance(currency);
    }

    /**
     * @see #setBalance(Currency, BigDecimal, Cause, Set) with {@link Collections#emptySet()}.
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        return setBalance(currency, amount, cause, Collections.emptySet());
    }

    /**
     * Sets the currency of the account according to {@link org.spongepowered.api.service.economy.account.Account#setBalance(Currency, BigDecimal, Cause, Set)}.
     * This method does not respect the contexts provided.
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (checkExceedsLowerBound(amount) || checkExceedsUpperBound(amount)) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_SPACE, TransactionTypes.DEPOSIT);
        }

        TransactionType transactionType;

        if (hasBalance(currency, contexts)) {
            transactionType = getBalance(currency).compareTo(amount) > 0 ? TransactionTypes.WITHDRAW : TransactionTypes.DEPOSIT;
        } else {
            transactionType = TransactionTypes.DEPOSIT;
        }

        accountManager.getAccountConfig()
                .getNode(accountUUID.toString(), CONF_BALANCES_SUBKEY, currency.getId())
                .setValue(amount.doubleValue());

        return new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, transactionType);
    }
}
