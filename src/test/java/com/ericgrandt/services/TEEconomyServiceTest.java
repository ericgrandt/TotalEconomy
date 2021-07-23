package com.ericgrandt.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.data.CurrencyData;
import com.ericgrandt.domain.TEAccount;
import com.ericgrandt.domain.TECurrency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class TEEconomyServiceTest {
    TEEconomyService sut;

    @Mock
    AccountData accountDataMock;

    @Mock
    CurrencyData currencyDataMock;

    @BeforeEach
    public void init() {
        sut = new TEEconomyService(accountDataMock, currencyDataMock);
    }

    @Test
    public void defaultCurrency_ShouldReturnCorrectCurrency() {
        TECurrency currency = new TECurrency(
            1,
            "Test",
            "Tests",
            "$",
            0,
            true
        );
        when(currencyDataMock.getDefaultCurrency()).thenReturn(currency);

        Currency result = sut.defaultCurrency();

        assertEquals(currency, result);
    }

    @Test
    public void currencies_ShouldReturnCorrectSetOfCurrencies() {
        Set<Currency> currencies = new HashSet<>();
        currencies.add(
            new TECurrency(
                1,
            "Test",
            "Tests",
            "$",
            0,
            true
            )
        );
        currencies.add(
            new TECurrency(
                2,
                "Test2",
                "Tests2",
                "A",
                0,
                false
            )
        );
        when(currencyDataMock.getCurrencies()).thenReturn(currencies);

        Set<Currency> result = sut.currencies();

        assertEquals(currencies, result);
    }

    @Test
    public void hasAccount_WithValidUuid_ShouldReturnTrue() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.hasAccount(any(UUID.class))).thenReturn(true);

        boolean result = sut.hasAccount(uuid);

        assertTrue(result);
    }

    @Test
    public void hasAccount_WithInvalidUuid_ShouldReturnFalse() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.hasAccount(any(UUID.class))).thenReturn(false);

        boolean result = sut.hasAccount(uuid);

        assertFalse(result);
    }

    @Test
    public void findOrCreateAccount_WithExistingUuid_ShouldReturnExistingAccount() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.hasAccount(any(UUID.class))).thenReturn(true);
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(account);

        Optional<UniqueAccount> result = sut.findOrCreateAccount(uuid);

        assertEquals(account, result.orElse(null));
    }

    @Test
    public void findOrCreateAccount_WithExistingUuid_ShouldNotAddAccount() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "Display Name",
            new HashMap<>()
        );

        when(accountDataMock.hasAccount(any(UUID.class))).thenReturn(true);
        when(accountDataMock.getAccount(any(UUID.class))).thenReturn(account);

        sut.findOrCreateAccount(uuid);

        verify(accountDataMock, times(0)).addAccount(any(TEAccount.class));
    }

    @Test
    public void findOrCreateAccount_WithNonExistingUuid_ShouldAddAccountAndReturnIt() {
        UUID uuid = UUID.randomUUID();
        UniqueAccount account = new TEAccount(
            uuid,
            "",
            new HashMap<>()
        );

        when(accountDataMock.hasAccount(uuid)).thenReturn(false);

        Optional<UniqueAccount> result = sut.findOrCreateAccount(uuid);

        verify(accountDataMock, times(1)).addAccount(any(TEAccount.class));
        assertEquals(account, result.orElse(null));
    }
}
