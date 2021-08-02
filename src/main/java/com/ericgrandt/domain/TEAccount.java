package com.ericgrandt.domain;

import com.google.common.base.Objects;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cause;
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
    public UUID uniqueId() {
        return userId;
    }

    @Override
    public String identifier() {
        return userId.toString();
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
        return hasBalance(currency, (Cause) null);
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause) {
        TECurrency teCurrency = (TECurrency) currency;
        return balances.containsKey(teCurrency);
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts) {
        return balance(currency, (Cause) null);
    }

    @Override
    public BigDecimal balance(Currency currency, Cause cause) {
        BigDecimal balance = balances.get(currency);

        if (balance == null) {
            return BigDecimal.ZERO;
        }

        return balance;
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts) {
        return balances((Cause) null);
    }

    @Override
    public Map<Currency, BigDecimal> balances(Cause cause) {
        return balances;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return setBalance(currency, amount, (Cause) null);
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        if (amount.compareTo(BigDecimal.ZERO) < 0 || !hasBalance(currency, cause)) {
            return new TETransactionResult(
                this,
                currency,
                balance(currency, cause),
                null,
                ResultType.FAILED,
                null
            );
        }

        balances.replace(currency, amount);

        return new TETransactionResult(
            this,
            currency,
            balance(currency, cause),
            null,
            ResultType.SUCCESS,
            null
        );
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return deposit(currency, amount, (Cause) null);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        BigDecimal currentBalance = balance(currency, cause);

        if (!hasBalance(currency, cause) || amount.compareTo(BigDecimal.ZERO) < 0) {
            return new TETransactionResult(
                this,
                currency,
                currentBalance,
                null,
                ResultType.FAILED,
                null
            );
        }

        return setBalance(currency, currentBalance.add(amount), cause);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return withdraw(currency, amount, (Cause) null);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        BigDecimal currentBalance = balance(currency, cause);

        if (!hasBalance(currency, cause) || amount.compareTo(BigDecimal.ZERO) < 0) {
            return new TETransactionResult(
                this,
                currency,
                currentBalance,
                null,
                ResultType.FAILED,
                null
            );
        }

        return setBalance(currency, currentBalance.subtract(amount), cause);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts) {
        return transfer(to, currency, amount, (Cause) null);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        if (!hasBalance(currency, cause)
            || !to.hasBalance(currency, cause)
            || amount.compareTo(BigDecimal.ZERO) < 0
        ) {
            return new TETransferResult(
                to,
                this,
                currency,
                balance(currency, cause),
                null,
                ResultType.FAILED,
                null
            );
        } else if (balance(currency, cause).compareTo(amount) < 0) {
            return new TETransferResult(
                to,
                this,
                currency,
                balance(currency, cause),
                null,
                ResultType.ACCOUNT_NO_FUNDS,
                null
            );
        }

        TransactionResult withdrawResult = withdraw(currency, amount, cause);
        to.deposit(currency, amount, cause);

        return new TETransferResult(
            to,
            this,
            currency,
            withdrawResult.amount(),
            null,
            ResultType.SUCCESS,
            null
        );
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Set<Context> contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause) {
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
