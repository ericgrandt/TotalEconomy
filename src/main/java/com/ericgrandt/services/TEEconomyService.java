package com.ericgrandt.services;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.domain.TEAccount;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

public class TEEconomyService implements EconomyService {
    private final AccountData accountData;
    private final CurrencyData currencyData;

    public TEEconomyService(AccountData accountData, CurrencyData currencyData) {
        this.accountData = accountData;
        this.currencyData = currencyData;
    }

    @Override
    public Currency defaultCurrency() {
        return currencyData.getDefaultCurrency();
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return accountData.hasAccount(uuid);
    }

    @Override
    public boolean hasAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
        if (hasAccount(uuid)) {
            return Optional.of(accountData.getAccount(uuid));
        }

        UniqueAccount account = new TEAccount(
            uuid,
            "", // TODO: Set the correct display name
            new HashMap<>()
        );
        accountData.addAccount(account);

        return Optional.of(account);
    }

    public Set<Currency> currencies() {
        return currencyData.getCurrencies();
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
        throw new UnsupportedOperationException();
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
