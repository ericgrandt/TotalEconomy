package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.common.econ.TransactionResult;
import java.math.BigDecimal;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.OfflinePlayer;

public class EconomyImpl implements Economy {
    private final boolean isEnabled;
    private final CurrencyDto defaultCurrency;
    private final CommonEconomy economy;

    public EconomyImpl(
        final boolean isEnabled,
        final CurrencyDto defaultCurrency,
        final CommonEconomy economy
    ) {
        this.isEnabled = isEnabled;
        this.defaultCurrency = defaultCurrency;
        this.economy = economy;
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
    public String currencyNamePlural() {
        return defaultCurrency.namePlural();
    }

    @Override
    public String currencyNameSingular() {
        return defaultCurrency.nameSingular();
    }

    @Override
    public String format(double amount) {
        Component component = economy.format(defaultCurrency, BigDecimal.valueOf(amount));
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return economy.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return economy.createAccount(player.getUniqueId());
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player.getUniqueId(), defaultCurrency.id()).doubleValue();
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
        TransactionResult result = economy.withdraw(
            player.getUniqueId(),
            defaultCurrency.id(),
            BigDecimal.valueOf(amount)
        );
        EconomyResponse.ResponseType responseType = result.resultType() == TransactionResult.ResultType.SUCCESS
            ? EconomyResponse.ResponseType.SUCCESS
            : EconomyResponse.ResponseType.FAILURE;

        return new EconomyResponse(
            amount,
            0,
            responseType,
            result.message()
        );
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        TransactionResult result = economy.deposit(
            player.getUniqueId(),
            defaultCurrency.id(),
            BigDecimal.valueOf(amount)
        );
        EconomyResponse.ResponseType responseType = result.resultType() == TransactionResult.ResultType.SUCCESS
            ? EconomyResponse.ResponseType.SUCCESS
            : EconomyResponse.ResponseType.FAILURE;

        return new EconomyResponse(
            amount,
            0,
            responseType,
            result.message()
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
}
