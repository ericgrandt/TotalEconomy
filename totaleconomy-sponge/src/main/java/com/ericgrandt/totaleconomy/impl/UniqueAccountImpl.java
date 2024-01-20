package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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
        if (!hasBalance(currency, contexts)) {
            return new TransactionResultImpl(this, currency, amount, ResultType.FAILED, null);
        }

        BigDecimal currentBalance = balances.get(currency);
        BigDecimal newBalance = currentBalance.add(amount);
        balances.put(currency, newBalance);

        try {
            balanceData.updateBalance(
                accountId,
                currencyDto.id(),
                newBalance.doubleValue()
            );
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, amount: %s)",
                    accountId,
                    currencyDto.id(),
                    newBalance
                ),
                e
            );
            return new TransactionResultImpl(this, currency, amount, ResultType.FAILED, null);
        }

        return new TransactionResultImpl(this, currency, amount, ResultType.SUCCESS, null);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        return deposit(currency, amount, new HashSet<>());
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts) {
        if (!hasBalance(currency, contexts)) {
            return new TransactionResultImpl(this, currency, amount, ResultType.FAILED, null);
        }

        BigDecimal currentBalance = balances.get(currency);
        BigDecimal newBalance = currentBalance.subtract(amount);
        balances.put(currency, newBalance);

        try {
            balanceData.updateBalance(
                accountId,
                currencyDto.id(),
                newBalance.doubleValue()
            );
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, amount: %s)",
                    accountId,
                    currencyDto.id(),
                    newBalance
                ),
                e
            );
            return new TransactionResultImpl(this, currency, amount, ResultType.FAILED, null);
        }

        return new TransactionResultImpl(this, currency, amount, ResultType.SUCCESS, null);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        return withdraw(currency, amount, new HashSet<>());
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0
            || !hasBalance(currency, contexts)
            || !to.hasBalance(currency, contexts)
        ) {
            return new TransferResultImpl(this, to, currency, amount, ResultType.FAILED, null);
        }

        BigDecimal currentFromBalance = balances.get(currency);
        if (currentFromBalance.compareTo(amount) < 0) {
            return new TransferResultImpl(this, to, currency, amount, ResultType.ACCOUNT_NO_FUNDS, null);
        }

        balances.put(currency, currentFromBalance.subtract(amount));

        BigDecimal currentToBalance = to.balance(currency, contexts);
        to.balances(contexts).put(currency, currentToBalance.add(amount));

        try {
            balanceData.transfer(
                accountId,
                UUID.fromString(to.identifier()),
                currencyDto.id(),
                amount.doubleValue()
            );
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling transfer (fromAccountId: %s, toAccountId: %s, currencyId: %s, amount: %s)",
                    accountId,
                    to.identifier(),
                    currencyDto.id(),
                    amount
                ),
                e
            );
            return new TransferResultImpl(this, to, currency, amount, ResultType.FAILED, null);
        }

        return new TransferResultImpl(this, to, currency, amount, ResultType.SUCCESS, null);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        return transfer(to, currency, amount, new HashSet<>());
    }

    @Override
    public String identifier() {
        return accountId.toString();
    }

    @Override
    public UUID uniqueId() {
        return accountId;
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        throw new UnsupportedOperationException();
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

        if (!Objects.equals(logger, that.logger)) {
            return false;
        }
        if (!Objects.equals(accountId, that.accountId)) {
            return false;
        }
        if (!Objects.equals(balances, that.balances)) {
            return false;
        }
        if (!Objects.equals(balanceData, that.balanceData)) {
            return false;
        }
        return Objects.equals(currencyDto, that.currencyDto);
    }
}
