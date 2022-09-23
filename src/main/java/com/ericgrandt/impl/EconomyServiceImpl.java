package com.ericgrandt.impl;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.VirtualAccountData;
import java.sql.SQLException;
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

public class EconomyServiceImpl implements EconomyService {
    private final Logger logger;
    private final AccountData accountData;
    private final VirtualAccountData virtualAccountData;

    public EconomyServiceImpl(Logger logger, AccountData accountData, VirtualAccountData virtualAccountData) {
        this.logger = logger;
        this.accountData = accountData;
        this.virtualAccountData = virtualAccountData;
    }

    @Override
    public Currency defaultCurrency() {
        // return currencyData.getDefaultCurrency();
        return null;
    }

    @Override
    public boolean hasAccount(UUID playerUUID) {
        try {
            return accountData.getAccount(playerUUID) != null;
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling hasAccount (playerUUID: %s)", playerUUID),
                e
            );

            return false;
        }
    }

    @Override
    public boolean hasAccount(String identifier) {
        try {
            return virtualAccountData.getVirtualAccount(identifier) != null;
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling hasAccount (identifier: %s)", identifier),
                e
            );

            return false;
        }
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
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
