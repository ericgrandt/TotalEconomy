package com.erigitic.domain;

import com.google.common.base.Objects;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

public class TEAccount implements UniqueAccount {
    private final UUID userId;
    private final String displayName;
    public final Map<Currency, BigDecimal> balances;

    public TEAccount(UUID userId, String displayName, Map<Currency, BigDecimal> balances) {
        this.userId = userId;
        this.displayName = displayName;
        this.balances = balances;
    }

    @Override
    public Component displayName() {
        return Component.text(displayName);
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        TECurrency teCurrency = (TECurrency) currency;
        return balances.containsKey(teCurrency);
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts) {
        TECurrency teCurrency = (TECurrency) currency;
        BigDecimal balance = balances.get(teCurrency);

        if (balance == null) {
            return BigDecimal.ZERO;
        }

        return balance;
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts) {
        return balances;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
            if (amount.compareTo(BigDecimal.ZERO) < 0 || !hasBalance(currency)) {
                return new TETransactionResult(
                    this,
                    currency,
                    balance(currency),
                    contexts,
                    ResultType.FAILED,
                    null
                );
            }

            balances.replace(currency, amount);

            return new TETransactionResult(
                this,
                currency,
                balance(currency),
                contexts,
                ResultType.SUCCESS,
                null
            );
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Set<Context> contexts) {
        BigDecimal currentBalance = balance(currency);

        if (!hasBalance(currency) || amount.compareTo(BigDecimal.ZERO) < 0) {
            return new TETransactionResult(
                this,
                currency,
                currentBalance,
                contexts,
                ResultType.FAILED,
                null
            );
        }

        return setBalance(currency, currentBalance.add(amount), contexts);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts) {
        BigDecimal currentBalance = balance(currency);

        if (!hasBalance(currency) || amount.compareTo(BigDecimal.ZERO) < 0) {
            return new TETransactionResult(
                this,
                currency,
                currentBalance,
                contexts,
                ResultType.FAILED,
                null
            );
        }

        return setBalance(currency, currentBalance.subtract(amount), contexts);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts) {
        if (!hasBalance(currency)
            || !to.hasBalance(currency)
            || amount.compareTo(BigDecimal.ZERO) < 0
        ) {
            return new TETransferResult(
                to,
                this,
                currency,
                balance(currency),
                contexts,
                ResultType.FAILED,
                null
            );
        } else if (balance(currency).compareTo(amount) < 0) {
            return new TETransferResult(
                to,
                this,
                currency,
                balance(currency),
                contexts,
                ResultType.ACCOUNT_NO_FUNDS,
                null
            );
        }

        TransactionResult withdrawResult = withdraw(currency, amount, contexts);
        to.deposit(currency, amount, contexts);

        return new TETransferResult(
            to,
            this,
            currency,
            withdrawResult.amount(),
            contexts,
            ResultType.SUCCESS,
            null
        );
    }

    @Override
    public String identifier() {
        return userId.toString();
    }

    @Override
    public UUID uniqueId() {
        return userId;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Context> activeContexts() {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TEAccount other = (TEAccount) o;
        return Objects.equal(userId, other.userId)
            && Objects.equal(displayName, other.displayName)
            && Objects.equal(balances, other.balances);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, displayName, balances);
    }
}
