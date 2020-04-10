package com.erigitic.economy;

import com.erigitic.TotalEconomy;
import com.erigitic.data.CurrencyData;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TEEconomyService implements EconomyService {
    private TotalEconomy plugin;
    private CurrencyData currencyData;

    public TEEconomyService() {
        this.plugin = TotalEconomy.getPlugin();
        this.currencyData = new CurrencyData(plugin.getDatabase());
    }

    @Override
    public Currency getDefaultCurrency() {
        return currencyData.getDefaultCurrency();
    }

    public Currency getCurrency(String id) {
        return currencyData.getCurrency(id);
    }

    @Override
    public Set<Currency> getCurrencies() {
        return currencyData.getCurrencies();
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
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        return Optional.empty();
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {

    }
}
