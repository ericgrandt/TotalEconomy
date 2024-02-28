package com.ericgrandt.totaleconomy.common.econ;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public class CommonEconomy {
    private final CommonLogger logger;
    private final AccountData accountData;
    private final BalanceData balanceData;
    private final CurrencyData currencyData;

    public CommonEconomy(
        final CommonLogger logger,
        final AccountData accountData,
        final BalanceData balanceData,
        final CurrencyData currencyData
    ) {
        this.logger = logger;
        this.accountData = accountData;
        this.balanceData = balanceData;
        this.currencyData = currencyData;
    }

    public CurrencyDto getDefaultCurrency() {
        try {
            return currencyData.getDefaultCurrency();
        } catch (SQLException e) {
            logger.error(
                "[Total Economy] Error calling getDefaultCurrency",
                e
            );
            return null;
        }
    }

    public boolean hasAccount(UUID uuid) {
        try {
            return accountData.getAccount(uuid) != null;
        } catch (SQLException e) {
            logger.error(
                String.format("[Total Economy] Error calling getAccount (accountId: %s)", uuid),
                e
            );
            return false;
        }
    }

    public boolean createAccount(UUID uuid, int currencyId) {
        try {
            return accountData.createAccount(uuid, currencyId);
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling createAccount (accountId: %s, currencyId: %s)",
                    uuid,
                    currencyId
                ),
                e
            );
            return false;
        }
    }

    public TransactionResult withdraw(UUID uuid, int currencyId, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(uuid, currencyId);
        if (currentBalance == null) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "No balance found"
            );
        }

        if (currentBalance.compareTo(amount) < 0) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "Insufficient funds"
            );
        }

        BigDecimal newBalance = currentBalance.subtract(amount);
        try {
            balanceData.updateBalance(uuid, currencyId, newBalance);
            return new TransactionResult(
                TransactionResult.ResultType.SUCCESS,
                ""
            );
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, newBalance: %s)",
                    uuid,
                    currencyId,
                    newBalance
                ),
                e
            );
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "An error occurred. Please contact an administrator."
            );
        }
    }

    public TransactionResult deposit(UUID uuid, int currencyId, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(uuid, currencyId);
        if (currentBalance == null) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "No balance found"
            );
        }

        BigDecimal newBalance = currentBalance.add(amount);
        try {
            balanceData.updateBalance(uuid, currencyId, newBalance);
            return new TransactionResult(
                TransactionResult.ResultType.SUCCESS,
                ""
            );
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, newBalance: %s)",
                    uuid,
                    currencyId,
                    newBalance
                ),
                e
            );
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "An error occurred. Please contact an administrator."
            );
        }
    }

    public boolean transfer(UUID uuid, UUID toUuid, int currencyId, double amount) {
        return true;
    }

    public BigDecimal getBalance(UUID uuid, int currencyId) {
        try {
            return balanceData.getBalance(uuid, currencyId);
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling getBalance (accountId: %s, currencyId: %s)",
                    uuid,
                    currencyId
                ),
                e
            );
            return null;
        }
    }

    public Component format(CurrencyDto currencyDto, double amount) {
        return Component.empty();
    }
}
