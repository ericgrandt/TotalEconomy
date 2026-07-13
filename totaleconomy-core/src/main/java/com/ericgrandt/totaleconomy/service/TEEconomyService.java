package com.ericgrandt.totaleconomy.service;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.dto.CreateAccountDto;
import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.dto.TransferResult;
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

    public TECurrency getDefaultCurrency() {
        try {
            return transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting default currency", e);
        }
    }

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

    public GetAccountBalanceResult getAccountBalance(UUID playerId, String currencyCode) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                var currency = currencyData.getCurrency(conn, currencyCode);
                var account = accountData.getAccount(conn, playerId, currency.code());

                return new GetAccountBalanceResult(currency, account.balance());
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting an account balance", e);
        }
    }

    public GetAccountBalanceResult getAccountBalance(UUID playerId) {
        try {
            var currency = transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
            return getAccountBalance(playerId, currency.code());
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting an account balance", e);
        }
    }

    public TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, String currencyCode, BigDecimal amount) {
        if (fromPlayerId.equals(toPlayerId)) {
            throw new SelfTransferException();
        }

        try {
            return transactionUtil.runInTransaction(conn -> {
                var currency = currencyData.getCurrency(conn, currencyCode);

                // Verify sending and receiving accounts actually exist
                accountData.getAccount(conn, fromPlayerId, currency.code());
                accountData.getAccount(conn, toPlayerId, currency.code());

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

    public TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, BigDecimal amount) {
        try {
            var currency = transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
            return transfer(fromPlayerId, toPlayerId, currency.code(), amount);
        } catch (SQLException e) {
            throw new DatabaseException("database exception while performing transfer", e);
        }
    }
}
