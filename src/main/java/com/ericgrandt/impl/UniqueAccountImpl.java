package com.ericgrandt.impl;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UniqueAccountImpl implements UniqueAccount {
    private final UUID playerUUID;
    private final Map<Currency, BigDecimal> balances;

    public UniqueAccountImpl(UUID playerUUID, Map<Currency, BigDecimal> balances) {
        this.playerUUID = playerUUID;
        this.balances = balances;
    }

    @Override
    public Component displayName() {
        return Component.text(playerUUID.toString());
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
        // UUID as a string
        return null;
    }

    @Override
    public UUID uniqueId() {
        return null;
    }
}