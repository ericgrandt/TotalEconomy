package com.ericgrandt.impl;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.BalanceData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.data.VirtualAccountData;
import com.ericgrandt.data.dto.AccountDto;
import com.ericgrandt.data.dto.CurrencyDto;
import com.ericgrandt.data.dto.VirtualAccountDto;
import java.sql.SQLException;
import java.util.Collection;
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
    private final CurrencyData currencyData;
    private final BalanceData balanceData;

    public EconomyServiceImpl(
        Logger logger,
        AccountData accountData,
        VirtualAccountData virtualAccountData,
        CurrencyData currencyData,
        BalanceData balanceData
    ) {
        this.logger = logger;
        this.accountData = accountData;
        this.virtualAccountData = virtualAccountData;
        this.currencyData = currencyData;
        this.balanceData = balanceData;
    }

    @Override
    public Currency defaultCurrency() {
        try {
            CurrencyDto currencyDto = currencyData.getDefaultCurrency();
            if (currencyDto == null) {
                return null;
            }

            return new CurrencyImpl(
                currencyDto.getId(),
                currencyDto.getNameSingular(),
                currencyDto.getNamePlural(),
                currencyDto.getSymbol(),
                currencyDto.getNumFractionDigits(),
                currencyDto.isDefault()
            );
        } catch (SQLException e) {
            logger.error(
                "Error calling getDefaultCurrency",
                e
            );
            return null;
        }
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
                logger,
                balanceData
            );
            return Optional.of(account);
        }

        Optional<AccountDto> createdAccountDto = createAndGetAccount(uuid);
        if (createdAccountDto.isPresent()) {
            UniqueAccount account = new UniqueAccountImpl(
                UUID.fromString(createdAccountDto.get().getId()),
                logger,
                balanceData
            );
            return Optional.of(account);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
        Optional<VirtualAccountDto> existingVirtualAccountDto = getVirtualAccount(identifier);
        if (existingVirtualAccountDto.isPresent()) {
            Account virtualAccount = new VirtualAccountImpl(existingVirtualAccountDto.get().getIdentifier());
            return Optional.of(virtualAccount);
        }

        Optional<VirtualAccountDto> createdVirtualAccountDto = createAndGetVirtualAccount(identifier);
        if (createdVirtualAccountDto.isPresent()) {
            Account virtualAccount = new VirtualAccountImpl(createdVirtualAccountDto.get().getIdentifier());
            return Optional.of(virtualAccount);
        }

        return Optional.empty();
    }

    @Override
    public Stream<UniqueAccount> streamUniqueAccounts() {
        try {
            return accountData.getAccounts()
                .stream()
                .map(account -> new UniqueAccountImpl(
                    UUID.fromString(account.getId()),
                    logger,
                    balanceData
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
        try {
            return virtualAccountData.getVirtualAccounts()
                .stream()
                .map(virtualAccount -> new VirtualAccountImpl(virtualAccount.getIdentifier()));
        } catch (SQLException e) {
            logger.error(
                "Error calling streamVirtualAccounts",
                e
            );
            return Stream.empty();
        }
    }

    @Override
    public Collection<VirtualAccount> virtualAccounts() {
        return streamVirtualAccounts().collect(Collectors.toList());
    }

    @Override
    public AccountDeletionResultType deleteAccount(UUID uuid) {
        try {
            accountData.deleteAccount(uuid);

            // NOTE: Regardless of if an account is deleted or not, as long as it doesn't error out we will return true.
            //  This may change in the future if a use case is found.
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
        try {
            virtualAccountData.deleteVirtualAccount(identifier);

            // NOTE: Regardless of if a virtual account is deleted or not, as long as it doesn't error out we will
            //  return true. This may change in the future if a use case is found.
            return new AccountDeletionResultTypeImpl(true);
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling deleteAccount (identifier: %s)", identifier),
                e
            );
            return new AccountDeletionResultTypeImpl(false);
        }
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

    private Optional<VirtualAccountDto> getVirtualAccount(String identifier) {
        try {
            return Optional.ofNullable(virtualAccountData.getVirtualAccount(identifier));
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling getVirtualAccount (identifier: %s)", identifier),
                e
            );
            return Optional.empty();
        }
    }

    private Optional<VirtualAccountDto> createAndGetVirtualAccount(String identifier) {
        try {
            virtualAccountData.createVirtualAccount(identifier);
            return Optional.ofNullable(virtualAccountData.getVirtualAccount(identifier));
        } catch (SQLException e) {
            logger.error(
                String.format("Error calling createAndGetVirtualAccount (identifier: %s)", identifier),
                e
            );
            return Optional.empty();
        }
    }
}
