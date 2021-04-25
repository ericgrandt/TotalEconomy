package com.erigitic.domain;

import java.math.BigDecimal;
import java.util.Set;
import com.google.common.base.Objects;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public class TETransferResult implements TransferResult {
    private final Account to;
    private final Account account;
    private final Currency currency;
    private final BigDecimal amount;
    private final Set<Context> contexts;
    private final ResultType resultType;
    private final TransactionType transactionType;

    // NOTE: TransactionType is currently always set to null since SpongeAPI is calling the static Sponge.game()
    // when using TransactionTypes.VALUE which makes it impossible to test. Is there a workaround?
    public TETransferResult(Account to, Account account, Currency currency, BigDecimal amount, Set<Context> contexts, ResultType resultType, TransactionType transactionType) {
        this.to = to;
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.resultType = resultType;
        this.transactionType = transactionType;
    }

    @Override
    public Account accountTo() {
        return to;
    }

    @Override
    public Account account() {
        return account;
    }

    @Override
    public Currency currency() {
        return currency;
    }

    @Override
    public BigDecimal amount() {
        return amount;
    }

    @Override
    public Set<Context> contexts() {
        return contexts;
    }

    @Override
    public ResultType result() {
        return resultType;
    }

    @Override
    public TransactionType type() {
        return transactionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TETransferResult other = (TETransferResult) o;
        return Objects.equal(to, other.to)
            && Objects.equal(account, other.account)
            && Objects.equal(currency, other.currency)
            && Objects.equal(amount, other.amount)
            && Objects.equal(contexts, other.contexts)
            && Objects.equal(resultType, other.resultType)
            && Objects.equal(transactionType, other.transactionType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(to, account, currency, amount, contexts, resultType, transactionType);
    }
}
