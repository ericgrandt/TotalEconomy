package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
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
    private final CurrencyDto currencyDto;
    private final CommonEconomy economy;
    private final BalanceData balanceData;

    private final Currency currency;

    // TODO: Replace data params with CommonEconomy
    public EconomyImpl(
        final Logger logger,
        final CurrencyDto currencyDto,
        final CommonEconomy economy,
        final BalanceData balanceData
    ) {
        this.logger = logger;
        this.currencyDto = currencyDto;
        this.economy = economy;
        this.balanceData = balanceData;

        this.currency = new CurrencyImpl(currencyDto);
    }

    @Override
    public Currency defaultCurrency() {
        return currency;
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return economy.hasAccount(uuid);
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
        if (!hasAccount(uuid)) {
            boolean accountCreated = economy.createAccount(uuid, currencyDto.id());
            if (!accountCreated) {
                return Optional.empty();
            }
        }

        BigDecimal balance = economy.getBalance(uuid, currencyDto.id());
        UniqueAccount account = new UniqueAccountImpl(
            logger,
            uuid,
            Map.of(currency, balance),
            balanceData,
            currencyDto,
            economy
        );
        return Optional.of(account);
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
