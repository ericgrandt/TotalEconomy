package com.erigitic.domain;

import java.math.BigDecimal;
import java.util.Set;
import com.google.common.base.Objects;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

public class TETransactionResult implements TransactionResult {
    private Account account;
    private Currency currency;
    private BigDecimal amount;
    private Set<Context> contexts;
    private ResultType resultType;
    private TransactionType transactionType;

    public TETransactionResult(Account account, Currency currency, BigDecimal amount, Set<Context> contexts, ResultType resultType, TransactionType transactionType) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.resultType = resultType;
        this.transactionType = transactionType;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Set<Context> getContexts() {
        return contexts;
    }

    @Override
    public ResultType getResult() {
        return resultType;
    }

    @Override
    public TransactionType getType() {
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
