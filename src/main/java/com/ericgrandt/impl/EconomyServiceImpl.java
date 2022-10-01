package com.ericgrandt.impl;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.VirtualAccountData;
import com.ericgrandt.data.dto.AccountDto;
import com.ericgrandt.data.dto.VirtualAccountDto;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
        Optional<AccountDto> existingAccountDto = getAccount(uuid);
        if (existingAccountDto.isPresent()) {
            UniqueAccount account = new UniqueAccountImpl(
                UUID.fromString(existingAccountDto.get().getId()),
                new HashMap<>()
            );
            return Optional.of(account);
        }

        Optional<AccountDto> createdAccountDto = createAndGetAccount(uuid);
        if (createdAccountDto.isPresent()) {
            UniqueAccount account = new UniqueAccountImpl(
                UUID.fromString(createdAccountDto.get().getId()),
                new HashMap<>()
            );
            return Optional.of(account);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
        Optional<VirtualAccountDto> virtualAccountDto = getVirtualAccount(identifier);
        Account account = new VirtualAccountImpl(
            virtualAccountDto.get().getIdentifier(),
            new HashMap<>()
        );
        return Optional.of(account);
    }

    @Override
    public Stream<UniqueAccount> streamUniqueAccounts() {
        try {
            return accountData.getAccounts()
                .stream()
                .map(account -> new UniqueAccountImpl(
                    UUID.fromString(account.getId()),
                    new HashMap<>()
                ));
        } catch (SQLException e) {
            logger.error(
                "Error calling streamUniqueAccounts",
                e
            );
            return Stream.empty();
        }
    }

    @Override
    public Collection<UniqueAccount> uniqueAccounts() {
        return streamUniqueAccounts().collect(Collectors.toList());
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
        try {
            accountData.deleteAccount(uuid);
            return new AccountDeletionResultTypeImpl(true);
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling deleteAccount (uuid: %s)", uuid),
                e
            );
            return new AccountDeletionResultTypeImpl(false);
        }
    }

    @Override
    public AccountDeletionResultType deleteAccount(String identifier) {
        return null;
    }

    private Optional<AccountDto> getAccount(UUID uuid) {
        try {
            return Optional.ofNullable(accountData.getAccount(uuid));
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling getAccount (uuid: %s)", uuid),
                e
            );
            return Optional.empty();
        }
    }

    private Optional<VirtualAccountDto> getVirtualAccount(String identifier) {
        try {
            return Optional.of(virtualAccountData.getVirtualAccount(identifier));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<AccountDto> createAndGetAccount(UUID uuid) {
        try {
            accountData.createAccount(uuid);
            return Optional.ofNullable(accountData.getAccount(uuid));
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling createAndGetAccount (uuid: %s)", uuid),
                e
            );
            return Optional.empty();
        }
    }
}
