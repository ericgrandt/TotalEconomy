package com.erigitic.config;

import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.*;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Erigitic on 1/1/2016.
 */
public class TEAccount implements UniqueAccount {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private UUID uuid;

    private ConfigurationNode accountConfig;

    public TEAccount(TotalEconomy totalEconomy, AccountManager accountManager, UUID uuid) {
        this.totalEconomy = totalEconomy;
        this.accountManager = accountManager;
        this.uuid = uuid;

        accountConfig = accountManager.getAccountConfig();
    }

    @Override
    public Text getDisplayName() {
        return Text.of(totalEconomy.getServer().getPlayer(uuid).get().getName());
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return totalEconomy.getStartingBalance();
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        if (accountConfig.getNode(uuid.toString(), currencyName + "-balance").getValue() != null) {
            return true;
        }

        return false;
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal balance = new BigDecimal(accountConfig.getNode(uuid.toString(), currencyName + "-balance").getString());

            return balance;
        }

        return null;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();

            accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(amount.setScale(2, BigDecimal.ROUND_DOWN));
            accountManager.saveAccountConfig();

            return new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
    }

    //TODO: Implement later
    @Override
    public TransactionResult resetBalances(Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return setBalance(currency, BigDecimal.ZERO, cause);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal curBalance = getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.add(amount);

            accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN));
            accountManager.saveAccountConfig();

            return new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal curBalance =  getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.subtract(amount);

            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN));
                accountManager.saveAccountConfig();

                return new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
            } else {
                return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
            }
        }

        return new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            BigDecimal curBalance = getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.subtract(amount);

            //TODO: Might not need to check if the balance is greater then zero here since it is being done in the withdraw function
            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                withdraw(currency, amount, cause, contexts);

                if (to.hasBalance(currency)) {
                    to.deposit(currency, amount, cause, contexts);

                    return new TETransferResult(this, to, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.TRANSFER);
                } else {
                    return new TETransferResult(this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
                }
            } else {
                return new TETransferResult(this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.TRANSFER);
            }
        }

        return new TETransferResult(this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getIdentifier() {
        return "";
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }
}
