package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public class UniqueAccountImpl implements UniqueAccount {
    private final UUID accountId;
    private final Map<Currency, BigDecimal> balances;
    private final BalanceData balanceData;
    private final CurrencyData currencyData;

    public UniqueAccountImpl(
        UUID accountId,
        Map<Currency, BigDecimal> balances,
        BalanceData balanceData,
        CurrencyData currencyData
    ) {
        this.accountId = accountId;
        this.balances = balances;
        this.balanceData = balanceData;
        this.currencyData = currencyData;
    }

    @Override
    public Component displayName() {
        return Component.text(accountId.toString());
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        // TODO: Just return the default balance from the config and ignore the passed in currency as we don't support
        //  multiple currencies
        return null;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return balances.containsKey(currency);
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause) {
        return balances.containsKey(currency);
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts) {
        if (!hasBalance(currency, contexts)) {
            return BigDecimal.ZERO;
        }

        return balances.get(currency);
    }

    @Override
    public BigDecimal balance(Currency currency, Cause cause) {
        if (!hasBalance(currency, cause)) {
            return BigDecimal.ZERO;
        }

        return balances.get(currency);
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts) {
        return balances;
    }

    @Override
    public Map<Currency, BigDecimal> balances(Cause cause) {
        return balances;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
        // update map, get currencyId, call balanceData.updateBalance()
        return null;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Set<Context> contexts) {
        return null;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause) {
        return null;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause) {
        return null;
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        return null;
    }

    @Override
    public String identifier() {
        return accountId.toString();
    }

    @Override
    public UUID uniqueId() {
        return accountId;
    }
}
