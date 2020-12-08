package com.erigitic.economy;

import com.erigitic.TotalEconomy;
import com.erigitic.data.AccountData;
import com.erigitic.data.CurrencyData;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

public class TEEconomyService implements EconomyService {
    private final TotalEconomy plugin;
    private final CurrencyData currencyData;
    private final AccountData accountData;

    public TEEconomyService() {
        this.plugin = TotalEconomy.getPlugin();
        this.currencyData = new CurrencyData(plugin.getDatabase());
        this.accountData = new AccountData(plugin.getDatabase());
    }

    @Override
    public Currency getDefaultCurrency() {
        return currencyData.getDefaultCurrency();
    }

    public Currency getCurrency(String currencyName) {
        return currencyData.getCurrency(currencyName);
    }

    @Override
    public Set<Currency> getCurrencies() {
        return currencyData.getCurrencies();
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        boolean hasAccount = false;

        if (accountData.getAccount(uuid.toString()).isPresent()) {
            hasAccount = true;
        }

        return hasAccount;
    }

    @Override
    public boolean hasAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {
        Optional<UniqueAccount> accountOpt = accountData.getAccount(uuid.toString());

        if (!accountOpt.isPresent()) {
            accountData.createAccount(uuid.toString());
        }

        return accountData.getAccount(uuid.toString());
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {

    }
}
