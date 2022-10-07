package com.ericgrandt.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public class VirtualAccountImpl implements VirtualAccount {
    private final String identifier;
    private final Map<Currency, BigDecimal> balances;

    public VirtualAccountImpl(String identifier, Map<Currency, BigDecimal> balances) {
        this.identifier = identifier;
        this.balances = balances;
    }

    @Override
    public Component displayName() {
        return Component.text(identifier);
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        return null;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return false;
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause) {
        return false;
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts) {
        return null;
    }

    @Override
    public BigDecimal balance(Currency currency, Cause cause) {
        return null;
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts) {
        return null;
    }

    @Override
    public Map<Currency, BigDecimal> balances(Cause cause) {
        return null;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
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
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VirtualAccountImpl that = (VirtualAccountImpl) o;

        if (!identifier.equals(that.identifier)) {
            return false;
        }
        return balances.equals(that.balances);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + balances.hashCode();
        return result;
    }
}
