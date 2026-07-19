package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.DatabaseException;
import com.ericgrandt.totaleconomy.model.Currency;
import com.ericgrandt.totaleconomy.service.EconomyService;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("deprecation")
public class VaultImpl implements Economy {
    private final Logger logger;
    private final EconomyService economyService;
    private final Currency currency;

    public VaultImpl(Logger logger, EconomyService economyService) {
        this.logger = logger;
        this.economyService = economyService;
        this.currency = economyService.getDefaultCurrency();
    }

    @Override
    public boolean isEnabled() {
        return true;
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
        return currency.fractionalDigits();
    }

    @Override
    public String format(double amount) {
        var component = currency.format(BigDecimal.valueOf(amount));
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Override
    public String currencyNamePlural() {
        return currency.pluralName();
    }

    @Override
    public String currencyNameSingular() {
        return currency.name();
    }


    @Override
    public boolean hasAccount(OfflinePlayer player) {
        try {
            return economyService.getAccountBalance(player.getUniqueId()) != null;
        } catch (AccountNotFoundException e) {
            return false;
        } catch (DatabaseException e) {
            logger.error("error while checking for account existence", e);
            return false;
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        try {
            return economyService.getAccountBalance(player.getUniqueId()).balance().doubleValue();
        } catch (AccountNotFoundException e) {
            return 0;
        } catch (DatabaseException e) {
            logger.error("error while getting balance", e);
            return 0;
        }
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        try {
            return economyService.getAccountBalance(player.getUniqueId()).balance().doubleValue() >= amount;
        } catch (AccountNotFoundException e) {
            return false;
        } catch (DatabaseException e) {
            logger.error("error while checking if player has sufficient funds", e);
            return false;
        }
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        throw new UnsupportedOperationException();
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
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {
        return 0;
    }

    @Override
    @Deprecated
    public double getBalance(String playerName, String world) {
        return 0;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Override
    @Deprecated
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    @Deprecated
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName) {
        return false;
    }
}
