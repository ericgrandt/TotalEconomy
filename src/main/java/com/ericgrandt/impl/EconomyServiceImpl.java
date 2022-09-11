package com.ericgrandt.impl;

import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class EconomyServiceImpl implements EconomyService {
    // private final CurrencyData currencyData;
    // private final AccountData accountData;
    //
    // public EconomyServiceImpl(CurrencyData currencyData, AccountData accountData) {
    //     this.currencyData = currencyData;
    //     this.accountData = accountData;
    // }

    @Override
    public Currency defaultCurrency() {
        // return currencyData.getDefaultCurrency();
        return null;
    }

    @Override
    public boolean hasAccount(UUID playerUUID) {
        // return accountData.hasAccount(playerUUID);
        return false;
    }

    @Override
    public boolean hasAccount(String identifier) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
        return Optional.empty();
    }

    @Override
    public Stream<UniqueAccount> streamUniqueAccounts() {
        return null;
    }

    @Override
    public Collection<UniqueAccount> uniqueAccounts() {
        return null;
    }

    @Override
    public Stream<VirtualAccount> streamVirtualAccounts() {
        return null;
    }

    @Override
    public Collection<VirtualAccount> virtualAccounts() {
        return null;
    }

    @Override
    public AccountDeletionResultType deleteAccount(UUID uuid) {
        return null;
    }

    @Override
    public AccountDeletionResultType deleteAccount(String identifier) {
        return null;
    }
}
