package com.ericgrandt.totaleconomy.common.econ;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public boolean createAccount(UUID uuid) {
        try {
            return accountData.createAccount(uuid);
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling createAccount (accountId: %s)",
                    uuid
                ),
                e
            );
            return false;
        }
    }

    public TransactionResult withdraw(UUID uuid, int currencyId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "Amount must be greater than zero"
            );
        }

        BigDecimal currentBalance = getBalance(uuid, currencyId);
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
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "Amount must be greater than zero"
            );
        }

        BigDecimal currentBalance = getBalance(uuid, currencyId);
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

    public TransactionResult transfer(UUID uuid, UUID toUuid, int currencyId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "Amount must be greater than zero"
            );
        }

        if (!hasAccount(toUuid)) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "User not found"
            );
        }

        BigDecimal currentBalance = getBalance(uuid, currencyId);
        if (currentBalance.compareTo(amount) < 0) {
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "Insufficient funds"
            );
        }

        try {
            balanceData.transfer(uuid, toUuid, currencyId, amount);
            return new TransactionResult(
                TransactionResult.ResultType.SUCCESS,
                ""
            );
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling transfer (accountId: %s, toAccountId: %s, currencyId: %s, newBalance: %s)",
                    uuid,
                    toUuid,
                    currencyId,
                    amount
                ),
                e
            );
            return new TransactionResult(
                TransactionResult.ResultType.FAILURE,
                "An error occurred. Please contact an administrator."
            );
        }
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
            return BigDecimal.ZERO;
        }
    }

    // TODO: Implement setBalance function
    public BigDecimal setBalance(UUID uuid, int currencyId, BigDecimal amount) {
        return null;
    }

    public Component format(CurrencyDto currencyDto, BigDecimal amount) {
        BigDecimal scaledAmount = amount.setScale(currencyDto.numFractionDigits(), RoundingMode.DOWN);
        return Component.text(currencyDto.symbol() + scaledAmount);
    }
}
