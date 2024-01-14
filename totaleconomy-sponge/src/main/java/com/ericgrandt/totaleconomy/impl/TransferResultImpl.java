package com.ericgrandt.totaleconomy.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public record TransferResultImpl(
    Account account,
    Account accountTo,
    Currency currency,
    BigDecimal amount,
    ResultType result,
    TransactionType type
) implements TransferResult {
    @Override
    public Set<Context> contexts() {
        return new HashSet<>();
    }
}
