package com.ericgrandt.totaleconomy.service;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.dto.CreateAccountDto;
import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.dto.TransferResult;
import com.ericgrandt.totaleconomy.dto.WithdrawResult;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.exception.InsufficientFundsException;
import com.ericgrandt.totaleconomy.exception.SelfTransferException;
import com.ericgrandt.totaleconomy.model.TEAccount;
import com.ericgrandt.totaleconomy.model.TECurrency;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class TEEconomyService implements EconomyService {
    private final TransactionUtil transactionUtil;
    private final CurrencyData currencyData;
    private final AccountData accountData;

    public TEEconomyService(TransactionUtil transactionUtil, CurrencyData currencyData, AccountData accountData) {
        this.transactionUtil = transactionUtil;
        this.currencyData = currencyData;
        this.accountData = accountData;
    }

    @Override
    public TECurrency getDefaultCurrency() {
        try {
            return transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting default currency", e);
        }
    }

    @Override
    public TEAccount createAccount(UUID playerId, String currencyCode) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                var startingBalance = currencyData.getCurrency(conn, currencyCode).startingBalance();
                return accountData.createAccount(
                    conn,
                    new CreateAccountDto(playerId, currencyCode, startingBalance)
                );
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while creating account", e);
        }
    }

    @Override
    public GetAccountBalanceResult getAccountBalance(UUID playerId, String currencyCode) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                var currency = currencyData.getCurrency(conn, currencyCode);
                var account = accountData.getAccount(conn, playerId, currency.code()).orElseThrow(
                    AccountNotFoundException::new
                );

                return new GetAccountBalanceResult(currency, account.balance());
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting an account balance", e);
        }
    }

    @Override
    public GetAccountBalanceResult getAccountBalance(UUID playerId) {
        try {
            var currency = transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
            return getAccountBalance(playerId, currency.code());
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting an account balance", e);
        }
    }

    @Override
    public WithdrawResult withdraw(UUID playerId, String currencyCode, BigDecimal amount) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                var currency = currencyData.getCurrency(conn, currencyCode);

                accountData.getAccount(conn, playerId, currency.code()).orElseThrow(AccountNotFoundException::new);

                var success = accountData.withdraw(conn, playerId, currency.code(), amount, true);
                if (!success) {
                    throw new InsufficientFundsException();
                }

                return new WithdrawResult(currency, amount);
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while withdrawing from account", e);
        }
    }

    @Override
    public WithdrawResult withdraw(UUID playerId, BigDecimal amount) {
        try {
            var currency = transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
            return withdraw(playerId, currency.code(), amount);
        } catch (SQLException e) {
            throw new DatabaseException("database exception while withdrawing from account", e);
        }
    }

    @Override
    public TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, String currencyCode, BigDecimal amount) {
        if (fromPlayerId.equals(toPlayerId)) {
            throw new SelfTransferException();
        }

        try {
            return transactionUtil.runInTransaction(conn -> {
                var currency = currencyData.getCurrency(conn, currencyCode);

                accountData.getAccount(conn, fromPlayerId, currency.code()).orElseThrow(AccountNotFoundException::new);
                accountData.getAccount(conn, toPlayerId, currency.code()).orElseThrow(AccountNotFoundException::new);

                var success = accountData.withdraw(conn, fromPlayerId, currency.code(), amount, true);
                if (!success) {
                    throw new InsufficientFundsException();
                }

                accountData.deposit(conn, toPlayerId, currency.code(), amount);

                return new TransferResult(currency, amount);
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while performing transfer", e);
        }
    }

    @Override
    public TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, BigDecimal amount) {
        try {
            var currency = transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
            return transfer(fromPlayerId, toPlayerId, currency.code(), amount);
        } catch (SQLException e) {
            throw new DatabaseException("database exception while performing transfer", e);
        }
    }
}
