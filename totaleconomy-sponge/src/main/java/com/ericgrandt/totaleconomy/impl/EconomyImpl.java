package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.AccountData;
import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.sql.SQLException;
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
    private final AccountData accountData;
    private final BalanceData balanceData;

    private final Currency currency;

    public EconomyImpl(
        Logger logger,
        CurrencyDto currencyDto,
        AccountData accountData,
        BalanceData balanceData
    ) {
        this.logger = logger;
        this.currencyDto = currencyDto;
        this.accountData = accountData;
        this.balanceData = balanceData;

        this.currency = new CurrencyImpl(currencyDto);
    }

    @Override
    public Currency defaultCurrency() {
        return currency;
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        try {
            return accountData.getAccount(uuid) != null;
        } catch (SQLException e) {
            logger.error(
                String.format("[Total Economy] Error calling getAccount (accountId: %s)", uuid),
                e
            );
            return false;
        }
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
        try {
            if (!hasAccount(uuid)) {
                accountData.createAccount(uuid, currencyDto.id());
            }

            BigDecimal balance = balanceData.getBalance(uuid, currencyDto.id());
            UniqueAccount account = new UniqueAccountImpl(
                logger,
                uuid,
                Map.of(currency, balance),
                balanceData,
                currencyDto
            );
            return Optional.of(account);
        } catch (SQLException e) {
            logger.error(
                String.format(
                    "[Total Economy] Error calling findOrCreateAccount (accountId: %s)",
                    uuid
                ),
                e
            );
            return Optional.empty();
        }
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
