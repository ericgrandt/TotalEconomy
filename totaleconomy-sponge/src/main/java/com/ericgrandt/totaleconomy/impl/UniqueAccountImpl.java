package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
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
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public class UniqueAccountImpl implements UniqueAccount {
    private final Logger logger;
    private final UUID accountId;
    private Map<Currency, BigDecimal> balances;
    private final BalanceData balanceData;
    private final CurrencyDto currencyDto;

    // TODO: Remove getCurrencyByName from CurrencyData
    public UniqueAccountImpl(
        Logger logger,
        UUID accountId,
        Map<Currency, BigDecimal> balances,
        BalanceData balanceData,
        CurrencyDto currencyDto
    ) {
        this.logger = logger;
        this.accountId = accountId;
        this.balances = balances;
        this.balanceData = balanceData;
        this.currencyDto = currencyDto;
    }

    @Override
    public Component displayName() {
        return Component.text(accountId.toString());
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        // TODO: Just return the default balance from the config and ignore the passed in currency as we don't support
        //  multiple currencies
        return null;
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
        if (!hasBalance(currency, contexts)) {
            return BigDecimal.ZERO;
        }

        return balances.get(currency);
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

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
        if (!hasBalance(currency, contexts)) {
            return new TransactionResultImpl(this, currency, amount, ResultType.FAILED, null);
        }

        balances.put(currency, amount);

        try {
            balanceData.updateBalance(
                accountId,
                currencyDto.id(),
                amount.doubleValue()
            );
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, amount: %s)",
                    accountId,
                    currencyDto.id(),
                    amount
                ),
                e
            );
            return new TransactionResultImpl(this, currency, amount, ResultType.FAILED, null);
        }

        return new TransactionResultImpl(this, currency, amount, ResultType.SUCCESS, null);
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        return setBalance(currency, amount, new HashSet<>());
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
        return accountId.toString();
    }

    @Override
    public UUID uniqueId() {
        return accountId;
    }
}
