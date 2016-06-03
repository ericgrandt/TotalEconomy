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
import java.util.*;

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

        return BigDecimal.ZERO;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        return new HashMap<Currency, BigDecimal>();
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;

        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();

            accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(amount.setScale(2, BigDecimal.ROUND_DOWN));
            accountManager.saveAccountConfig();

            transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
            totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

            return transactionResult;
        }

        transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult = new TETransactionResult(this, accountManager.getDefaultCurrency(), BigDecimal.ZERO, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        //TODO: Do something different here?
        Map result = new HashMap<>();
        result.put(accountManager.getDefaultCurrency(), transactionResult);

        return result;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return setBalance(currency, BigDecimal.ZERO, cause);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;

        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal curBalance = getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.add(amount);

            accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN));

            // Reset balance to the money cap if it goes over
            if (totalEconomy.isLoadMoneyCap()) {
                if (getBalance(currency, contexts).compareTo(totalEconomy.getMoneyCap()) == 1) {
                    accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(totalEconomy.getMoneyCap());
                }
            }
            
            accountManager.saveAccountConfig();

            transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
            totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

            return transactionResult;
        }

        transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;

        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();
            BigDecimal curBalance =  getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.subtract(amount);

            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                accountConfig.getNode(uuid.toString(), currencyName + "-balance").setValue(newBalance.setScale(2, BigDecimal.ROUND_DOWN));
                accountManager.saveAccountConfig();

                transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

                return transactionResult;
            } else {
                transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.DEPOSIT);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

                return transactionResult;
            }
        }

        transactionResult = new TETransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransferResult transferResult;

        if (hasBalance(currency, contexts)) {
            BigDecimal curBalance = getBalance(currency, contexts);
            BigDecimal newBalance = curBalance.subtract(amount);

            //TODO: Might not need to check if the balance is greater then zero here since it is being done in the withdraw function
            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                withdraw(currency, amount, cause, contexts);

                if (to.hasBalance(currency)) {
                    to.deposit(currency, amount, cause, contexts);

                    transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.TRANSFER);
                    totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

                    return transferResult;
                } else {
                    transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
                    totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

                    return transferResult;
                }
            } else {
                transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.TRANSFER);
                totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

                return transferResult;
            }
        }

        transferResult = new TETransferResult(this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transferResult));

        return transferResult;
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return new HashSet<Context>();
    }
}
