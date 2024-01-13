package com.ericgrandt.totaleconomy.impl;

import java.math.BigDecimal;
import java.util.Set;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

public record TransactionResultImpl(
    Account account,
    Currency currency,
    BigDecimal amount,
    ResultType result,
    TransactionType type
) implements TransactionResult {
    @Override
    public Set<Context> contexts() {
        throw new UnsupportedOperationException();
    }
}
