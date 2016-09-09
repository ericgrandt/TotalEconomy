package com.erigitic.config;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Eric on 1/2/2016.
 */
public class TETransferResult implements TransferResult {

    private Account account;
    private Account to;
    private Currency currency;
    private BigDecimal amount;
    private Set<Context> contexts;
    private ResultType resultType;
    private TransactionType transactionType;

    public TETransferResult(Account account, Account to, Currency currency, BigDecimal amount, Set<Context> contexts,
                            ResultType resultType, TransactionType transactionType) {
        this.account = account;
        this.to = to;
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
    public Account getAccountTo() {
        return to;
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
}
