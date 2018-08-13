/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.config.account;

import com.erigitic.config.TETransactionResult;
import com.erigitic.except.TEConfigurationException;
import com.erigitic.except.TEConnectionException;
import com.erigitic.main.TotalEconomy;
import com.erigitic.sql.TESqlManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of {@link TEAccountBase} which queries the database via {@link TESqlManager#getAccountsConnection()}.
 * At the moment there is no caching implemented. Therefore the data is always up to date.
 */
public class TEDatabaseAccount extends TEAccountBase {

    private static final String QUERY_ADD_ACCOUNT = "INSERT INTO accounts (`uuid`, `display_name`) VALUES (?, ?)";
    private static final String QUERY_BALANCE = "SELECT `balance` FROM ? WHERE `uuid` = ? and `currency` = ?";
    private static final String QUERY_SET_BALANCE = "UPDATE ? SET `balance` = ? WHERE `uuid` = ? and `currency` = ?";
    private static final String QUERY_ADD_BALANCE = "INSERT INTO ? (`uuid`, `currency`, `balance`) VALUES (?, ?, ?)";

    private TESqlManager sqlManager;

    public TEDatabaseAccount(TotalEconomy totalEconomy, UUID accountUUID) {
        super(totalEconomy, accountUUID);
        this.sqlManager = totalEconomy.getTeSqlManager();
    }

    /**
     * @see TEAccountBase#create()
     */
    @Override
    public void create() {
        try (PreparedStatement statement = sqlManager.getAccountsConnection().prepareStatement(QUERY_ADD_ACCOUNT)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new TEConnectionException("Failed to create account: " + accountUUID.toString());
        }
    }

    /**
     * @see org.spongepowered.api.service.economy.account.UniqueAccount#hasBalance(Currency)
     */
    @Override
    public boolean hasBalance(Currency currency) {
        return super.hasBalance(currency);
    }

    /**
     * Checks if a balance entry is present in the database for a given currency.
     *
     * @see org.spongepowered.api.service.economy.account.UniqueAccount#hasBalance(Currency, Set)
     */
    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        try (PreparedStatement statement = sqlManager.getAccountsConnection().prepareStatement(QUERY_BALANCE)) {
            statement.setString(1, getBalanceTableName());
            statement.setString(2, accountUUID.toString());
            statement.setString(3, currency.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return false;
                } else {
                    if (resultSet.next()) {
                        throw new TEConfigurationException("Ambiguous rows! Multiple rows for uuid and currency: "
                                + accountUUID.toString() + "/" + currency.getId());
                    }
                    return true;
                }
            }

        } catch (SQLException e) {
            throw new TEConnectionException("Failed to check balance for: " + accountUUID, e);
        }
    }

    /**
     * @see org.spongepowered.api.service.economy.account.UniqueAccount#getBalance(Currency)
     */
    @Override
    public BigDecimal getBalance(Currency currency) {
        return super.getBalance(currency);
    }

    /**
     * @see org.spongepowered.api.service.economy.account.UniqueAccount#getBalance(Currency, Set)
     */
    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        try (PreparedStatement statement = sqlManager.getAccountsConnection().prepareStatement(QUERY_BALANCE)) {
            statement.setString(1, getBalanceTableName());
            statement.setString(2, accountUUID.toString());
            statement.setString(3, currency.getId());

            BigDecimal balance;

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    balance = getDefaultBalance(currency);
                } else {
                    balance = resultSet.getBigDecimal(1);

                    if (resultSet.next()) {
                        throw new TEConfigurationException("Ambiguous rows! Multiple rows for uuid and currency: "
                                + accountUUID.toString() + "/" + currency.getId());
                    }
                }
                return balance;
            }
        } catch (SQLException e) {
            throw new TEConnectionException("Failed to get balance for: " + accountUUID, e);
        }
    }

    /**
     * @see #setBalance(Currency, BigDecimal, Cause, Set) with {@link Collections#emptySet()}.
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        return setBalance(currency, amount, cause, Collections.emptySet());
    }

    /**
     * Sets the currency of the account according to {@link org.spongepowered.api.service.economy.account.Account#setBalance(Currency, BigDecimal, Cause, Set)}.
     * This method does not respect the contexts provided.
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        if (checkExceedsLowerBound(amount) || checkExceedsUpperBound(amount)) {
            return new TETransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_SPACE, TransactionTypes.DEPOSIT);
        }

        TransactionType transactionType;
        boolean failed = false;

        if (hasBalance(currency, contexts)) {
            transactionType = getBalance(currency).compareTo(amount) > 0 ? TransactionTypes.WITHDRAW : TransactionTypes.DEPOSIT;
            try (PreparedStatement statement = sqlManager.getAccountsConnection().prepareStatement(QUERY_SET_BALANCE)) {
                statement.setString(1, getBalanceTableName());
                statement.setBigDecimal(2, amount);
                statement.setString(3, accountUUID.toString());
                statement.setString(4, currency.getId());

                failed = statement.executeUpdate() != 1;

            } catch (SQLException e) {
                totalEconomy.getLogger().warn("Failed to set balance for: " + accountUUID, e);
                failed = true;
            }

        } else {
            transactionType = TransactionTypes.DEPOSIT;
            try (PreparedStatement statement = sqlManager.getAccountsConnection().prepareStatement(QUERY_ADD_BALANCE)) {
                statement.setString(1, getBalanceTableName());
                statement.setString(2, accountUUID.toString());
                statement.setString(3, currency.getId());
                statement.setBigDecimal(4, amount);

                failed = statement.executeUpdate() != 1;

            } catch (SQLException e) {
                totalEconomy.getLogger().warn("Failed to set balance for: " + accountUUID, e);
                failed = true;
            }
        }

        return new TETransactionResult(this, currency, amount, contexts, failed ? ResultType.FAILED : ResultType.SUCCESS, transactionType);
    }

    /**
     * Convenience method to get the balance table name with its correct prefix.
     */
    private String getBalanceTableName() {
        return sqlManager.getTablePrefix() + "balances";
    }
}
