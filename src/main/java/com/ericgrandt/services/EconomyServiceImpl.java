package com.ericgrandt.services;

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
    public EconomyServiceImpl() {
        
    }

    @Override
    public Currency defaultCurrency() {
        return null;
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return false;
    }

    @Override
    public boolean hasAccount(String identifier) {
        return false;
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
