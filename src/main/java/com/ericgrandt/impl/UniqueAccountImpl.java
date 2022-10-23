package com.ericgrandt.impl;

import com.ericgrandt.data.BalanceData;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public class UniqueAccountImpl implements UniqueAccount {
    private final UUID id;
    private final Logger logger;
    private final BalanceData balanceData;

    public UniqueAccountImpl(UUID id, Logger logger, BalanceData balanceData) {
        this.id = id;
        this.logger = logger;
        this.balanceData = balanceData;
    }

    @Override
    public Component displayName() {
        return Component.text(id.toString());
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        CurrencyImpl currencyImpl = (CurrencyImpl) currency;

        try {
            return balanceData.getDefaultBalance(currencyImpl.getId());
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling getDefaultBalance (currencyId: %s)", currencyImpl.getId()),
                e
            );
            return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        CurrencyImpl currencyImpl = (CurrencyImpl) currency;

        try {
            return balanceData.getBalance(id, currencyImpl.getId()) != null;
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "Error calling getBalance (accountId: %s, currencyId: %s)",
                    id,
                    currencyImpl.getId()
                ),
                e
            );
            return false;
        }
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause) {
        return hasBalance(currency, new HashSet<>());
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
        return id.toString();
    }

    @Override
    public UUID uniqueId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UniqueAccountImpl that = (UniqueAccountImpl) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
