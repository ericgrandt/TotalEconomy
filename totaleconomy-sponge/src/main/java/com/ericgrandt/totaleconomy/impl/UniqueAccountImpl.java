package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public class UniqueAccountImpl implements UniqueAccount {
    private final SpongeWrapper spongeWrapper;
    private final UUID accountId;
    private final CommonEconomy economy;
    private final int defaultCurrencyId;
    private final Map<Currency, BigDecimal> balances;

    public UniqueAccountImpl(
        final SpongeWrapper spongeWrapper,
        final UUID accountId,
        final CommonEconomy economy,
        final int defaultCurrencyId,
        final Map<Currency, BigDecimal> balances
    ) {
        this.spongeWrapper = spongeWrapper;
        this.accountId = accountId;
        this.economy = economy;
        this.defaultCurrencyId = defaultCurrencyId;
        this.balances = balances;
    }

    @Override
    public Component displayName() {
        return Component.text(accountId.toString());
    }

    @Override
    public String identifier() {
        return accountId.toString();
    }

    @Override
    public UUID uniqueId() {
        return accountId;
    }

    // TODO: Create CommonEconomy function to get default balance
    @Override
    public BigDecimal defaultBalance(Currency currency) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return balances.containsKey(currency);
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause) {
        return hasBalance(currency, new HashSet<>());
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts) {
        return balances.getOrDefault(currency, BigDecimal.ZERO);
    }

    @Override
    public BigDecimal balance(Currency currency, Cause cause) {
        return balance(currency, new HashSet<>());
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts) {
        return balances;
    }

    @Override
    public Map<Currency, BigDecimal> balances(Cause cause) {
        return balances(new HashSet<>());
    }

    // TODO: Implement
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
        throw new NotImplementedException("");
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        return setBalance(currency, amount, new HashSet<>());
    }

    // TODO: Implement
    @Override
    public Map<Currency, TransactionResult> resetBalances(Set<Context> contexts) {
        throw new NotImplementedException("");
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause) {
        return resetBalances(new HashSet<>());
    }

    // TODO: Implement
    @Override
    public TransactionResult resetBalance(Currency currency, Set<Context> contexts) {
        throw new NotImplementedException("");
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause) {
        return resetBalance(currency, new HashSet<>());
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Set<Context> contexts) {
        com.ericgrandt.totaleconomy.common.econ.TransactionResult result = economy.deposit(
            accountId,
            defaultCurrencyId,
            amount
        );
        ResultType resultType = result.resultType() == com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS
            ? ResultType.SUCCESS
            : ResultType.FAILED;

        return new TransactionResultImpl(
            this,
            currency,
            amount,
            resultType,
            spongeWrapper.deposit()
        );
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        return deposit(currency, amount, new HashSet<>());
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts) {
        com.ericgrandt.totaleconomy.common.econ.TransactionResult result = economy.withdraw(
            accountId,
            defaultCurrencyId,
            amount
        );
        ResultType resultType = result.resultType() == com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS
            ? ResultType.SUCCESS
            : ResultType.FAILED;

        return new TransactionResultImpl(
            this,
            currency,
            amount,
            resultType,
            spongeWrapper.withdraw()
        );
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        return withdraw(currency, amount, new HashSet<>());
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts) {
        com.ericgrandt.totaleconomy.common.econ.TransactionResult result = economy.transfer(
            accountId,
            UUID.fromString(to.identifier()),
            defaultCurrencyId,
            amount
        );
        ResultType resultType = result.resultType() == com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS
            ? ResultType.SUCCESS
            : ResultType.FAILED;

        return new TransferResultImpl(
            this,
            to,
            currency,
            amount,
            resultType,
            spongeWrapper.transfer()
        );
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        return transfer(to, currency, amount, new HashSet<>());
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

        if (!Objects.equals(spongeWrapper, that.spongeWrapper)) {
            return false;
        }
        if (!Objects.equals(accountId, that.accountId)) {
            return false;
        }
        if (!Objects.equals(economy, that.economy)) {
            return false;
        }
        return Objects.equals(balances, that.balances);
    }
}
