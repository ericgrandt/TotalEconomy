package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

public class EconomyImpl implements EconomyService {
    private final SpongeWrapper spongeWrapper;
    private final CurrencyDto currencyDto;
    private final CommonEconomy economy;

    private final Currency currency;

    // TODO: Replace data params with CommonEconomy
    public EconomyImpl(
        final SpongeWrapper spongeWrapper,
        final CurrencyDto currencyDto,
        final CommonEconomy economy
    ) {
        this.spongeWrapper = spongeWrapper;
        this.currencyDto = currencyDto;
        this.economy = economy;

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
            spongeWrapper,
            uuid,
            economy,
            currencyDto.id(),
            Map.of(currency, balance)
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
