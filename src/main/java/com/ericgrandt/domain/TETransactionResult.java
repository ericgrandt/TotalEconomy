package com.ericgrandt.domain;

import com.google.common.base.Objects;
import java.math.BigDecimal;
import java.util.Set;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

public class TETransactionResult implements TransactionResult {
    private final Account account;
    private final Currency currency;
    private final BigDecimal amount;
    private final Set<Context> contexts;
    private final ResultType resultType;
    private final TransactionType transactionType;

    public TETransactionResult(Account account, Currency currency, BigDecimal amount, Set<Context> contexts, ResultType resultType, TransactionType transactionType) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.resultType = resultType;
        this.transactionType = transactionType;
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

        TETransactionResult other = (TETransactionResult) o;
        return Objects.equal(account, other.account)
            && Objects.equal(currency, other.currency)
            && Objects.equal(amount, other.amount)
            && Objects.equal(contexts, other.contexts)
            && Objects.equal(resultType, other.resultType)
            && Objects.equal(transactionType, other.transactionType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(account, currency, amount, contexts, resultType, transactionType);
    }
}
