package com.erigitic.domain;

import com.erigitic.economy.TETransactionResult;
import com.google.common.base.Objects;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TEAccount implements UniqueAccount {
    private final String userId;
    private final String displayName;
    public final Map<Currency, BigDecimal> balances;

    public TEAccount(String userId, String displayName, Map<Currency, BigDecimal> balances) {
        this.userId = userId;
        this.displayName = displayName;
        this.balances = balances;
    }

    @Override
    public Text getDisplayName() {
        return Text.of(displayName);
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        TECurrency teCurrency = (TECurrency) currency;
        return balances.containsKey(teCurrency);
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        TECurrency teCurrency = (TECurrency) currency;
        BigDecimal balance = balances.get(teCurrency);

        if (balance == null) {
            return BigDecimal.ZERO;
        }

        return balance;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        return balances;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (amount.compareTo(BigDecimal.ZERO) < 0 || !hasBalance(currency)) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        }

        balances.replace(currency, amount);

        return new TETransactionResult(
            this,
            currency,
            amount,
            contexts,
            ResultType.SUCCESS,
            TransactionTypes.DEPOSIT
        );
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public String getIdentifier() {
        return userId;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return UUID.fromString(userId);
    }

    public void addBalance(TECurrency currency, BigDecimal balance) {
        this.balances.put(currency, balance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TEAccount other = (TEAccount) o;
        return Objects.equal(userId, other.userId) &&
            Objects.equal(displayName, other.displayName) &&
            Objects.equal(balances, other.balances);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, displayName, balances);
    }

    @Override
    public TransferResult transfer(org.spongepowered.api.service.economy.account.Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }
}
