package com.ericgrandt.totaleconomy.service;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.dto.CreateAccountRequest;
import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.model.TEAccount;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class EconomyService {
    private final TransactionUtil transactionUtil;
    private final CurrencyData currencyData;
    private final AccountData accountData;

    public EconomyService(TransactionUtil transactionUtil, CurrencyData currencyData, AccountData accountData) {
        this.transactionUtil = transactionUtil;
        this.currencyData = currencyData;
        this.accountData = accountData;
    }

    // TODO: Any reason to return a *Result dto here? Or are we good with just TEAccount?
    // TODO: Test
    public TEAccount createAccount(UUID playerId, String currencyCode) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                // TODO: Think about ditching the request dto here and just passing in the values?
                return accountData.createAccount(
                    conn,
                    new CreateAccountRequest(playerId, currencyCode, BigDecimal.ZERO)
                );
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while creating account", e);
        }
    }

    public GetAccountBalanceResult getAccountBalance(UUID playerId, String currencyCode) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                var currency = currencyCode != null ? currencyData.getCurrency(
                    conn,
                    currencyCode
                ) : currencyData.getDefaultCurrency(conn);
                var account = accountData.getAccount(conn, playerId, currency.code());

                return new GetAccountBalanceResult(currency, account.balance());
            });
        } catch (SQLException e) {
            throw new DatabaseException("database exception while getting an account balance", e);
        }
    }
}
