package com.erigitic.economy;

import com.erigitic.TotalEconomy;
import com.erigitic.data.AccountData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.*;
import org.spongepowered.api.text.Text;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TEAccount implements UniqueAccount {
    private TotalEconomy plugin;
    private AccountData accountData;
    private UUID uuid;

    public TEAccount(UUID uuid) {
        this.uuid = uuid;

        plugin = TotalEconomy.getPlugin();
        accountData = new AccountData(plugin.getDatabase());
    }

    @Override
    public Text getDisplayName() {
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(uuid);

        return playerOpt.isPresent() ? Text.of(playerOpt.get().getName()) : Text.EMPTY;
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        BigDecimal balance = accountData.getBalance(currency.getId(), uuid.toString());

        return balance != null;
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        BigDecimal balance = accountData.getBalance(currency.getId(), uuid.toString());

        return balance;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        Map<Currency, BigDecimal> balances = accountData.getBalances(uuid.toString());

        return balances;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        int rowsAffected = accountData.setBalance(
            currency.getId(),
            uuid.toString(),
            amount.setScale(0, RoundingMode.HALF_DOWN)
        );

        if (rowsAffected <= 0) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, null);
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, null);
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        throw new NotImplementedException();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        throw new NotImplementedException();
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal currentBalance = accountData.getBalance(currency.getId(), uuid.toString());
        if (currentBalance == null) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        }

        BigDecimal newBalance = currentBalance.add(amount).setScale(0, RoundingMode.HALF_DOWN);
        TransactionResult result = setBalance(currency, newBalance, cause, contexts);
        if (result.getResult() == ResultType.FAILED) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        }

        // TODO: Check if number is too big? And return ResultType.ACCOUNT_NO_SPACE?

        return new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal currentBalance = accountData.getBalance(currency.getId(), uuid.toString());
        if (currentBalance == null) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
        }

        BigDecimal newBalance = currentBalance.subtract(amount).setScale(0, RoundingMode.HALF_DOWN);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
        }

        TransactionResult result = setBalance(currency, newBalance, cause, contexts);
        if (result.getResult() == ResultType.FAILED) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        BigDecimal currentBalance = accountData.getBalance(currency.getId(), uuid.toString());
        if (currentBalance == null) {
            return new TETransferResult(to, this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
        }

        BigDecimal newBalance = currentBalance.subtract(amount).setScale(0, RoundingMode.HALF_DOWN);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return new TETransferResult(to, this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.TRANSFER);
        }

        BigDecimal toBalance = to.getBalance(currency);
        BigDecimal newToBalance = toBalance.add(amount).setScale(0, RoundingMode.HALF_DOWN);

        int rowsAffected = accountData.transfer(currency.getId(), getIdentifier(), to.getIdentifier(), newBalance, newToBalance);
        if (rowsAffected < 2) {
            return new TETransferResult(to, this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
        }

        return new TETransferResult(to, this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.TRANSFER);
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<>();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }
}
