package com.ericgrandt.totaleconomy.economy;

import com.ericgrandt.totaleconomy.Economy;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.dto.CreateAccountRequest;
import com.ericgrandt.totaleconomy.dto.GetAccountRequest;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.exception.EntityNotFoundException;
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import com.ericgrandt.totaleconomy.model.Account;
import com.ericgrandt.totaleconomy.model.Currency;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class EconomyProvider implements Economy {
    private final Logger logger;
    private final TransactionUtil transactionUtil;
    private final CurrencyData currencyData;
    private final AccountData accountData;

    public EconomyProvider(
        Logger logger,
        TransactionUtil transactionUtil,
        CurrencyData currencyData,
        AccountData accountData
    ) {
        this.logger = logger;
        this.transactionUtil = transactionUtil;
        this.currencyData = currencyData;
        this.accountData = accountData;
    }

    @Override
    public Currency getDefaultCurrency() {
        try {
            return transactionUtil.runInTransaction(currencyData::getDefaultCurrency);
        } catch (EntityNotFoundException e) {
            logger.error("default currency not found", e);
            throw new MissingDefaultCurrencyException(e);
        } catch (SQLException e) {
            logger.error("database exception when getting default currency", e);
            throw new DatabaseException(e);
        }
    }

    @Override
    public Currency getCurrency(String currencyCode) {
        try {
            return transactionUtil.runInTransaction(conn -> currencyData.getCurrency(conn, currencyCode));
        } catch (EntityNotFoundException e) {
            logger.error("currency not found", e);
            throw new CurrencyNotFoundException(e);
        } catch (SQLException e) {
            logger.error("database exception when getting currency", e);
            throw new DatabaseException(e);
        }
    }

    @Override
    public Account createAccount(UUID playerId, String currencyCode, BigDecimal balance) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                var req = new CreateAccountRequest(playerId, currencyCode, balance);
                return accountData.createAccount(conn, req);
            });
        } catch (SQLException e) {
            logger.error("database exception when creating account", e);
            throw new DatabaseException(e);
        }
    }

    @Override
    public Account getAccount(UUID playerId, String currencyCode) {
        try {
            return transactionUtil.runInTransaction(conn -> {
                var req = new GetAccountRequest(playerId, currencyCode);
                return accountData.getAccount(conn, req);
            });
        } catch (EntityNotFoundException e) {
            logger.error("account not found", e);
            throw new AccountNotFoundException(e);
        } catch (SQLException e) {
            logger.error("database exception when getting account", e);
            throw new DatabaseException(e);
        }
    }
}
