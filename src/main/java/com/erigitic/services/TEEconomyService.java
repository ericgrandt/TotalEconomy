package com.erigitic.services;

import com.erigitic.data.AccountData;
import com.erigitic.data.CurrencyData;
import com.erigitic.domain.TEAccount;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

public class TEEconomyService implements EconomyService {
    private final AccountData accountData;
    private final CurrencyData currencyData;

    public TEEconomyService(AccountData accountData, CurrencyData currencyData) {
        this.accountData = accountData;
        this.currencyData = currencyData;
    }

    @Override
    public Currency getDefaultCurrency() {
        return currencyData.getDefaultCurrency();
    }

    @Override
    public Set<Currency> getCurrencies() {
        return currencyData.getCurrencies();
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return accountData.getAccount(uuid) != null;
    }

    @Override
    public boolean hasAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        // NOTE: This ends up making two of the same database calls but could become one if we didn't use hasAccount
        if (hasAccount(uuid)) {
            return Optional.of(accountData.getAccount(uuid));
        }

        UniqueAccount account = new TEAccount(
            uuid,
            "",
            new HashMap<>()
        );
        accountData.addAccount(account);

        return Optional.of(account);
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {
        throw new UnsupportedOperationException();
    }
}
