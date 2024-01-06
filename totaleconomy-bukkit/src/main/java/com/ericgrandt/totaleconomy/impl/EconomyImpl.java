package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.OfflinePlayer;

public class EconomyImpl implements Economy {
    private final Logger logger;
    private final boolean isEnabled;
    private final CurrencyDto defaultCurrency;
    private final AccountData accountData;
    private final BalanceData balanceData;

    public EconomyImpl(
        Logger logger,
        boolean isEnabled,
        CurrencyDto defaultCurrency,
        AccountData accountData,
        BalanceData balanceData
    ) {
        this.logger = logger;
        this.isEnabled = isEnabled;
        this.defaultCurrency = defaultCurrency;
        this.accountData = accountData;
        this.balanceData = balanceData;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "Total Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return defaultCurrency.numFractionDigits();
    }

    @Override
    public String format(double amount) {
        BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount)
            .setScale(defaultCurrency.numFractionDigits(), RoundingMode.DOWN);

        return String.format("%s%s", defaultCurrency.symbol(), bigDecimalAmount);
    }

    @Override
    public String currencyNamePlural() {
        return defaultCurrency.namePlural();
    }

    @Override
    public String currencyNameSingular() {
        return defaultCurrency.nameSingular();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();

        try {
            return accountData.getAccount(playerUUID) != null;
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format("[Total Economy] Error calling getAccount (accountId: %s)", playerUUID),
                e
            );
            return false;
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();
        int currencyId = 1;

        try {
            return accountData.createAccount(playerUUID, currencyId);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling createAccount (accountId: %s, currencyId: %s)",
                    playerUUID,
                    currencyId
                ),
                e
            );
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();
        int currencyId = 1;

        BigDecimal balance = getBigDecimalBalance(playerUUID, currencyId);
        if (balance == null) {
            return 0;
        }

        return balance.doubleValue();
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        UUID playerUUID = player.getUniqueId();
        int currencyId = 1;

        BigDecimal currentBalance = getBigDecimalBalance(playerUUID, currencyId);
        if (currentBalance == null) {
            return new EconomyResponse(
                amount,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "No balance found for user"
            );
        }

        double newBalance = currentBalance.doubleValue() - amount;
        if (!updateBalance(playerUUID, currencyId, newBalance)) {
            return new EconomyResponse(
                amount,
                currentBalance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Error updating balance"
            );
        }

        return new EconomyResponse(
            amount,
            newBalance,
            EconomyResponse.ResponseType.SUCCESS,
            null
        );
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        UUID playerUUID = player.getUniqueId();
        int currencyId = 1;

        BigDecimal currentBalance = getBigDecimalBalance(playerUUID, currencyId);
        if (currentBalance == null) {
            return new EconomyResponse(
                amount,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "No balance found for user"
            );
        }

        double newBalance = currentBalance.doubleValue() + amount;
        if (!updateBalance(playerUUID, currencyId, newBalance)) {
            return new EconomyResponse(
                amount,
                currentBalance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Error updating balance"
            );
        }

        return new EconomyResponse(
            amount,
            newBalance,
            EconomyResponse.ResponseType.SUCCESS,
            null
        );
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean hasAccount(String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getBalance(String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getBalance(String playerName, String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean has(String playerName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        throw new UnsupportedOperationException();
    }

    public CurrencyDto getDefaultCurrency() {
        return defaultCurrency;
    }

    private BigDecimal getBigDecimalBalance(UUID playerUUID, int currencyId) {
        try {
            return balanceData.getBalance(playerUUID, currencyId);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling getBalance (accountId: %s, currencyId: %s)",
                    playerUUID,
                    currencyId
                ),
                e
            );
            return null;
        }
    }

    private boolean updateBalance(UUID playerUUID, int currencyId, double newBalance) {
        try {
            balanceData.updateBalance(playerUUID, currencyId, newBalance);
            return true;
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling updateBalance (accountId: %s, currencyId: %s, balance: %s)",
                    playerUUID,
                    currencyId,
                    newBalance
                ),
                e
            );
            return false;
        }
    }
}
