package com.ericgrandt.services;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.domain.TEAccount;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

        // TODO: It's currently super difficult to get the display name and then unit test it.
        // TODO: Figure out how to handle this when/if needed.
        Map<Currency, BigDecimal> balances = new HashMap<>();
        currencyData.getCurrencies().forEach(currency -> {
        	balances.put(currency, BigDecimal.ZERO);
        });
        TEAccount account = new TEAccount(
        	accountData,
            uuid,
            "",
            balances
        );
        accountData.addAccount(account);

        return Optional.of(account);
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<UniqueAccount> streamUniqueAccounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<UniqueAccount> uniqueAccounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<VirtualAccount> streamVirtualAccounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<VirtualAccount> virtualAccounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccountDeletionResultType deleteAccount(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccountDeletionResultType deleteAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    public Set<Currency> currencies() {
        return currencyData.getCurrencies();
    }
}
