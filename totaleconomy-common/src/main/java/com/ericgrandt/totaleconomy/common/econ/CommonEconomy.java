package com.ericgrandt.totaleconomy.common.econ;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.logger.CommonLogger;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

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
}
