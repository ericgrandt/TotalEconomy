package com.erigitic.config;

import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.service.economy.Currency;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TECurrencyRegistryModule implements CatalogRegistryModule<Currency> {

    private TotalEconomy totalEconomy;

    public TECurrencyRegistryModule(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
    }

    @Override
    public Optional<Currency> getById(String id) {
        for (Currency currency : totalEconomy.getCurrencies()) {
            if (currency.getId().equals(id)) {
                return Optional.of(currency);
            }
        }

        return Optional.empty();
    }

    @Override
    public Collection<Currency> getAll() {
        return totalEconomy.getCurrencies();
    }
}
