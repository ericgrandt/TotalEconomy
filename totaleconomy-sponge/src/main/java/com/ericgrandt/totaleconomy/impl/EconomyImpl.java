package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

public class EconomyImpl implements EconomyService {
    private final Logger logger;
    private final CurrencyImpl currency;
    private final AccountData accountData;
    private final BalanceData balanceData;

    public EconomyImpl(
        Logger logger,
        CurrencyDto currencyDto,
        AccountData accountData,
        BalanceData balanceData
    ) {
        this.logger = logger;
        this.currency = new CurrencyImpl(currencyDto);
        this.accountData = accountData;
        this.balanceData = balanceData;
    }

    @Override
    public Currency defaultCurrency() {
        return currency;
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return false;
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
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
    public AccountDeletionResultType deleteAccount(UUID uuid) {
        return null;
    }

    @Override
    public boolean hasAccount(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
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
    public AccountDeletionResultType deleteAccount(String identifier) {
        throw new UnsupportedOperationException();
    }
}
