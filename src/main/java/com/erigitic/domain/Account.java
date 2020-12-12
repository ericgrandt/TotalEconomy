package com.erigitic.domain;

import com.erigitic.economy.TETransactionResult;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Account implements UniqueAccount {
    public UUID uuid;
    public String displayName;
    public List<Balance> balances;

    public Account(UUID uuid, String displayName, List<Balance> balances) {
        this.uuid = uuid;
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
        return balances.stream()
            .anyMatch(balance -> balance.currencyId == Integer.parseInt(currency.getId()));
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        Balance balanceForCurrency = balances.stream()
            .filter(balance -> balance.currencyId == Integer.parseInt(currency.getId()))
            .findFirst()
            .orElse(null);

        if (balanceForCurrency != null) {
            return balanceForCurrency.balance;
        }

        return BigDecimal.ZERO;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        }

        Balance balanceForCurrency = balances.stream()
            .filter(balance -> balance.currencyId == Integer.parseInt(currency.getId()))
            .findFirst()
            .orElse(null);

        if (balanceForCurrency != null) {
            balanceForCurrency.balance = amount;

            return new TETransactionResult(
                this,
                currency,
                amount,
                contexts,
                ResultType.SUCCESS,
                TransactionTypes.DEPOSIT
            );
        }

        return new TETransactionResult(
            this,
            currency,
            amount,
            contexts,
            ResultType.FAILED,
            TransactionTypes.DEPOSIT
        );
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        throw new UnsupportedOperationException();
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
    public TransferResult transfer(org.spongepowered.api.service.economy.account.Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }
}
