package com.erigitic.config.account;

import com.erigitic.config.TEEconomyTransactionEvent;
import com.erigitic.config.TETransactionResult;
import com.erigitic.main.TotalEconomy;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;

/**
 * Created by MarkL4YG on 08-Jan-18
 */
public class TESqlAccount extends TEAccountBase {

    protected Logger logger;
    protected DataSource dataSource;

    public TESqlAccount(TotalEconomy totalEconomy, Logger logger, DataSource dataSource, UUID uniqueID) {
        super(totalEconomy, uniqueID);
        this.logger = logger;
        this.dataSource = dataSource;
    }

    /**
     * Determines if a balance exists for a {@link Currency}
     *
     * @param currency Currency type to be checked for
     * @param contexts
     * @return boolean If a balance exists for the specified currency
     */
    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        try (Statement statement = dataSource.getConnection().createStatement()) {
            String query = "SELECT `balance` FROM balances WHERE `uid` = :account_uid AND `currency` = :currency_name";
            query = query.replaceAll(":currency_name", currencyName);
            query = query.replaceAll(":account_uid", getUniqueId().toString());

            statement.execute(query);
            try (ResultSet result = statement.getResultSet()) {
                return result.isBeforeFirst();
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to check for balance existence", e);
        }
    }

    /**
     * Gets the balance of a {@link Currency}
     *
     * @param currency The currency to get the balance of
     * @param contexts
     * @return BigDecimal The balance
     */
    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        try (Statement statement = dataSource.getConnection().createStatement()) {
            String query = "SELECT `balance` FROM balances WHERE `uid` = :account_uid AND `currency` = :currency_name";
            query = query.replaceAll(":currency_name", currencyName);
            query = query.replaceAll(":account_uid", getUniqueId().toString());

            statement.execute(query);
            try (ResultSet result = statement.getResultSet()) {

                // Return the default balance when none is saved
                if (!result.isBeforeFirst()) {
                    return getDefaultBalance(currency);
                }

                return result.getBigDecimal("balance");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to check for balance existence", e);
        }
    }

    /**
     * Sets the balance of a {@link Currency}
     *
     * @param currency Currency to set the balance of
     * @param amount Amount to set the balance to
     * @param cause
     * @param contexts
     * @return TransactionResult Result of the transaction
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        // If the amount is greater then the money cap, set the amount to the money cap
        amount = amount.min(totalEconomy.getMoneyCap());

        // Does the balance exist?
        // No? Insert the row.
        if (hasBalance(currency, contexts)) {
            BigDecimal delta = amount.subtract(getBalance(currency));
            TransactionType transactionType = delta.compareTo(BigDecimal.ZERO) >= 0 ? TransactionTypes.DEPOSIT : TransactionTypes.WITHDRAW;

            boolean successful = false;

            try (Statement statement = dataSource.getConnection().createStatement()) {
                String query = "UPDATE balances SET `balance` = :balance WHERE `uid` = :account_uid AND `currency` = :currency_name";
                query = query.replaceAll(":currency_name", currencyName);
                query = query.replaceAll(":account_uid", getUniqueId().toString());
                query = query.replaceAll(":balance", amount.toString());

                statement.executeUpdate(query);
                successful = statement.getUpdateCount() == 1;

                if (!successful) {
                    throw new SQLException("Update count mismatched");
                }

            } catch (Exception e) {
                logger.error("Failed to set balance for account " + getUniqueId().toString(), e);
                return new TETransactionResult(this, currency, BigDecimal.ZERO, contexts, ResultType.FAILED, transactionType);
            }

            transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.SUCCESS, transactionType);

        } else {

            TransactionType transactionType = amount.compareTo(BigDecimal.ZERO) >= 0 ? TransactionTypes.DEPOSIT : TransactionTypes.WITHDRAW;

            boolean successful = false;

            try (Statement statement = dataSource.getConnection().createStatement()) {
                String query = "INSERT INTO balances (`uid`, `currency`, `balance`) VALUES(:account_uid, :currency, :balance)";
                query = query.replaceAll(":currency_name", currencyName);
                query = query.replaceAll(":account_uid", getUniqueId().toString());
                query = query.replaceAll(":balance", amount.toString());

                statement.executeUpdate(query);
                successful = statement.getUpdateCount() == 1;

                if (!successful) {
                    throw new SQLException("Update count mismatched");
                }

            } catch (Exception e) {
                logger.error("Failed to set balance for account " + getUniqueId().toString(), e);
                return new TETransactionResult(this, currency, BigDecimal.ZERO, contexts, ResultType.FAILED, transactionType);
            }

            transactionResult = new TETransactionResult(this, currency, amount.abs(), contexts, ResultType.SUCCESS, transactionType);
        }

        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    /**
     * Gets the display name associated with the account
     *
     * @return Text The display name
     */
    @Override
    public Text getDisplayName() {

        if (!isVirtual()) {
            UserStorageService userStore = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            return userStore.get(getUniqueId()).<Text>map(user -> Text.of(user.getName()))
                       .orElseGet(() -> Text.of("ERR_PLAYER_NAME"));
        }

        try (Statement statement = dataSource.getConnection().createStatement()){

            String query = "SELECT `displayname` FROM `accounts` WHERE `uid` = :account_uid";
            query = query.replaceAll("account_uid", getUniqueId().toString());

            statement.execute(query);

            try (ResultSet result = statement.getResultSet()) {
                String displayName = result.getString("displayname");
                return TextSerializers.PLAIN.deserialize(displayName);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve account display name", e);
        }
    }

    public void setDisplayName(Text displayName) {

        if (!isVirtual()) {
            throw new IllegalStateException("Setting displaynames for non-virtual accounts is not allowed!");
        }

        try (Statement statement = dataSource.getConnection().createStatement()){

            String query = "UPDATE `accounts` SET `displayname` = :displayname WHERE `uid` = :account_uid";
            query = query.replaceAll("account_uid", getUniqueId().toString());
            query = query.replaceAll("displayname", TextSerializers.PLAIN.serialize(displayName));

            statement.execute(query);

            if (statement.getUpdateCount() != 1) {
                throw new SQLException("Update count mismatched");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to set account display name", e);
        }
    }
}
